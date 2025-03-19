package generator.ui.dialog;

import com.intellij.database.psi.DbTable;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import freemarker.template.Template;
import generator.config.ScopeState;
import generator.config.TemplateConfig;
import generator.data.table.TableData;
import generator.interfaces.impl.GlobalStateService;
import generator.interfaces.impl.layout.DoubleColumnLayout;
import generator.interfaces.impl.layout.SingleColumnLayout;
import generator.ui.components.ListCheckboxComponent;
import generator.util.NameUtil;
import generator.util.StaticUtil;
import generator.util.TemplateUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenerateConfigDialog extends DialogWrapper {
    private final ScopeState scopeState;
    private final Project project;
    private final AnActionEvent event;
    private JPanel contentPane;
    private JLabel templateGroupLabel;
    private JLabel typeMappingLabel;
    private JLabel pathLabel;
    private JTextField pathInput;
    private JButton chooseButton;
    private JComboBox<String> templateGroupSelected;
    private JComboBox<String> typeMappingSelected;
    private JScrollPane tableScrollPanel;
    private JScrollPane generateTemplatePanel;
    private JButton templateChoose;
    private JButton refreshButton;
    private JButton selectAllButton;
    private JButton clearSelectAllButton;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    private void refreshTemplateGroupSelect() {
        templateGroupSelected.setEditable(true);

        templateGroupSelected.removeAllItems();

        var globalState = GlobalStateService.getInstance().getState();

        for (String input : globalState.getHistoryUsePath()) {
            templateGroupSelected.insertItemAt(input, 0);
        }
        if (templateGroupSelected.getItemCount() > 0) {
            templateGroupSelected.setSelectedIndex(0);
        }
    }

    private void initTypeMappingSelect() {
        var globalState = GlobalStateService.getInstance().getState();

        globalState.getGroupMapTemplate().forEach((k, v) -> typeMappingSelected.addItem(k));
    }

    private void refreshGenerateTemplatePanel() {
        var templatePath = scopeState.getTemplateGroupPath();

        if (templatePath == null) {
            return;
        }

        var globalState = GlobalStateService.getInstance().getState();

        var dir = Path.of(templatePath);
        var file = dir.toFile();
        if (!file.exists()) {
            templateGroupSelected.removeItem(templatePath);
            globalState.getHistoryUsePath().remove(templatePath);
            return;
        }

        try (var templateGroup = Files.list(dir)) {
            if (!templatePath.isBlank()) {
                 if (!globalState.getHistoryUsePath().contains(templatePath)) {
                    templateGroupSelected.insertItemAt(templatePath, 0);
                }
                globalState.getHistoryUsePath().add(templatePath);
            }

            var paths = new HashMap<String, Path>();

            var collect = templateGroup.peek(x -> paths.put(x.getFileName().toString(), x)).map(x -> x.getFileName().toString()).collect(Collectors.toList());

            var tables = new ListCheckboxComponent(new DoubleColumnLayout(), collect);

            for (var actionListener : selectAllButton.getActionListeners()) {
                selectAllButton.removeActionListener(actionListener);
            }
            selectAllButton.addActionListener(e -> Objects.requireNonNull(tables.getCheckBoxList()).forEach(x -> x.setSelected(true)));

            for (var actionListener : clearSelectAllButton.getActionListeners()) {
                clearSelectAllButton.removeActionListener(actionListener);
            }
            clearSelectAllButton.addActionListener(e -> Objects.requireNonNull(tables.getCheckBoxList()).forEach(x -> x.setSelected(false)));
            generateTemplatePanel.setViewportView(tables);
            scopeState.setTemplateFilePath(paths, tables);

            Objects.requireNonNull(scopeState.getTemplateGroup()).setSelectedItem(templatePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initDatabaseTreeState() {
        //获取选中的所有表
        PsiElement[] psiElements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }
        List<DbTable> dbTableList = new ArrayList<>();
        for (PsiElement element : psiElements) {
            if (!(element instanceof DbTable dbTable)) {
                continue;
            }
            dbTableList.add(dbTable);
        }
        if (dbTableList.isEmpty()) {
            return;
        }

        // 获取当前项目所有数据源
        var allTables = new HashMap<String, DbTable>(16);

        var model = new DefaultListModel<String>();
        for (var table : dbTableList) {
            allTables.put(table.getName(), table);

            model.addElement(table.getName());

        }

        var tables = new ListCheckboxComponent(new SingleColumnLayout(), allTables.keySet());
        tableScrollPanel.setViewportView(tables);

        scopeState.setAllTableAndComponent(allTables, tables);

    }

    @Override
    protected void doOKAction() {
        var globalState = GlobalStateService.getInstance().getState();

        var fileNameMapTemplate = new HashMap<String, String>();
        try {
            for (var path : scopeState.getSelectedTemplatePath()) {
                var strings = Files.readString(path);
                if (strings.length() > TemplateUtil.SPLIT_TAG.length() + 1) {
                    var fileName = path.getFileName().toString().split("\\.")[0];

                    fileNameMapTemplate.put(fileName, strings);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var mapperTemplates = globalState.getTemplates(scopeState.getSelectTypeMapping());
        if (mapperTemplates.isEmpty()) {
            JOptionPane.showMessageDialog(null, "TypeMapper is not empty", "Message！", JOptionPane.WARNING_MESSAGE);
            return;
        }

        scopeState.getSelectedTables().parallelStream().map(x -> new TableData(x, mapperTemplates)).forEach(tableData -> {
            for (var entry : fileNameMapTemplate.entrySet()) {
                try {
                    Template template = new Template(entry.getKey(), entry.getValue(), TemplateUtil.cfg);
                    var root = new HashMap<String, Object>();
                    root.put("table", tableData);
                    root.put("columns", tableData.getColumns());
                    root.put("NameUtil", new NameUtil());

                    String sourceCode;
                    try (var bo = new ByteArrayOutputStream()) {
                        template.process(root, new OutputStreamWriter(bo, StandardCharsets.UTF_8));
                        sourceCode = bo.toString(StandardCharsets.UTF_8);
                    }

                    var extracted = TemplateUtil.extractConfig(TemplateUtil.SPLIT_TAG_REGEX, sourceCode);
                    var templateConfig = extracted.component1();
                    sourceCode = extracted.component2();

                    var outPath = Path.of(scopeState.getPath(),
                            StringUtil.isEmpty(templateConfig.getDir()) ? "Temp" : templateConfig.getDir(),
                            StringUtil.isEmpty(templateConfig.getFileName()) ? entry.getKey() : templateConfig.getFileName());

                    var file = outPath.toFile();
                    if (!file.exists()) {
                        file.getParentFile().mkdirs();

                        if (!file.createNewFile()) {
                            StaticUtil.showWarningNotification("Create File", "Can not create file", project, NotificationType.WARNING);
                            return;
                        }
                    }

                    try (var writer = new OutputStreamWriter(new FileOutputStream(outPath.toString()), StandardCharsets.UTF_8)) {
                        writer.write(sourceCode);
                        writer.flush();
                    }
                    StaticUtil.showWarningNotification("Template Generate", entry.getKey() + " Generate Success", project, NotificationType.INFORMATION);

                } catch (Exception e) {
                    Messages.showErrorDialog(e.getMessage(), "Error");

                    throw new RuntimeException(e);
                }
            }
        });

        super.doOKAction();
    }

    public GenerateConfigDialog(AnActionEvent event) {
        super(event.getProject());
        this.event = event;
        this.project = event.getProject();
        scopeState = new ScopeState(project, pathInput, templateGroupSelected, typeMappingSelected);
        setModal(true);

        Function<ActionEvent, Optional<VirtualFile>> fileChooserConsumer = e -> {
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            descriptor.setForcedToUseIdeaFileChooser(true);
            descriptor.setTitle("选择路径");
            // 2. 弹出路径选择器
            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            return Optional.ofNullable(virtualFile);
        };

        chooseButton.addActionListener(e -> fileChooserConsumer.apply(e).ifPresent(x -> scopeState.setGenerateFileStorePath(x.getPath())));

        templateGroupSelected.addActionListener(e -> refreshGenerateTemplatePanel());
        templateChoose.addActionListener(e -> fileChooserConsumer.apply(e).ifPresent(x -> scopeState.setTemplateGroupPath(x.getPath())));

        refreshTemplateGroupSelect();

        initDatabaseTreeState();

        initTypeMappingSelect();

        refreshButton.addActionListener(e -> refreshGenerateTemplatePanel());

        init();
    }
}
