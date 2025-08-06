package generator.ui.dialog;

import com.intellij.database.model.DasNamed;
import com.intellij.database.model.DasTable;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import freemarker.core.StopException;
import generator.config.ScopeState;
import generator.data.ComboBoxSelect;
import generator.data.GenerateContext;
import generator.data.ScoredMember;
import generator.data.TemplateContextWrapper;
import generator.data.table.TableData;
import generator.interfaces.IHistorySelectedDelegate;
import generator.interfaces.impl.GlobalStateService;
import generator.interfaces.impl.HistoryStateService;
import generator.ui.components.ListCheckboxComponent;
import generator.ui.components.SelectionHistoryComboBox;
import generator.ui.layout.DoubleColumnLayout;
import generator.ui.layout.SingleColumnLayout;
import generator.util.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private JButton tableSelectButton;
    private JTextField namespaceTextField;
    private JCheckBox namespaceLockCheckBox;
    private JComboBox<String> namespaceSplitChar;

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        AtomicBoolean hasCloseDialog = new AtomicBoolean(true);
        try {
            if (scopeState.getSelectedTables().isEmpty()) {
                Messages.showErrorDialog("Table is required", "Error");
                return;
            }

            var globalState = GlobalStateService.getInstance().getState();

            var fileNameMapTemplate = new HashMap<Path, String>();
            for (var path : scopeState.getSelectedTemplatePath()) {
                var fileContent = Files.readString(path);
                if (fileContent.length() > TemplateUtil.SPLIT_TAG.length() + 1) {
                    fileNameMapTemplate.put(path, fileContent);
                }
            }

            var mapperTemplates = globalState.getTypeMappingGroupMap().getOrDefault(scopeState.getSelectTypeMapping(), Set.of()).stream().sorted().toList();
            if (mapperTemplates.isEmpty()) {
                Messages.showErrorDialog("TypeMapper is empty", "Error");
                return;
            }

            // build context
            var datasource = DbUtil.getDatasource(DbUtil.getAllDatasource(project), scopeState.getSelectedTables().stream().findFirst().orElseThrow());

            var mapperUtil = new MapperUtil(mapperTemplates);
            var dbContext = new GenerateContext(mapperTemplates, datasource);

            var fullSelectTables = scopeState.getSelectedTables().stream().map(x -> new TableData(x, dbContext)).toList();
            var templateSharedContext = new TemplateContextWrapper();

            // process template
            scopeState.getSelectedTables().stream().map(x -> new TableData(x, dbContext)).forEach(tableData -> {

                fileNameMapTemplate.entrySet().stream().sorted((Map.Entry.comparingByKey())).forEachOrdered(entry -> {
                    var templatePath = entry.getKey().toString().replace(StringUtil.defaultIfEmpty(scopeState.getTemplateGroupPath(), ""), "");

                    try {
                        //region process template context
                        var context = new HashMap<String, Object>();
                        context.put("context", templateSharedContext);
                        context.put("namespace", namespaceTextField.getText());
                        context.put("dbms", datasource.getDbms());
                        context.put("selectedTables", fullSelectTables);
                        context.put("table", tableData);
                        context.put("columns", tableData.getColumns());
                        context.put("NameUtil", NameUtil.INSTANCE);
                        context.put("MapperUtil", mapperUtil);

                        String sourceCode;
                        try (var bo = new ByteArrayOutputStream()) {
                            TemplateUtil.evaluate(context, new OutputStreamWriter(bo, StandardCharsets.UTF_8), templatePath, entry.getValue());
                            sourceCode = bo.toString(StandardCharsets.UTF_8);
                        }

                        var extracted = TemplateUtil.extractConfig(TemplateUtil.SPLIT_TAG_REGEX, sourceCode);
                        var templateConfig = extracted.component1();
                        sourceCode = extracted.component2();

                        //endregion

                        var fileName = StringUtil.isEmpty(templateConfig.getFileName())
                                ? entry.getKey().getFileName().toString() : templateConfig.getFileName();

                        var outFilePath = StringUtil.isEmpty(templateConfig.getDir())
                                ? StaticUtil.extractDirAfterName(templatePath.substring(0, templatePath.lastIndexOf(FileSystems.getDefault().getSeparator()) + 1), String.valueOf(scopeState.getTemplateGroupPath()))
                                .split("[/\\\\]")
                                : templateConfig.getDir().split("[/\\\\]");

                        outFilePath = ArrayUtil.append(outFilePath, fileName);

                        var outPath = Path.of(scopeState.getPath(), outFilePath);

                        var file = outPath.toFile();
                        if (!file.exists()) {
                            var ignore = file.getParentFile().mkdirs();

                            if (!file.createNewFile()) {
                                StaticUtil.showWarningNotification("Create File", "Can not create file", project, NotificationType.WARNING);
                                return;
                            }
                        }

                        try (var writer = new OutputStreamWriter(new FileOutputStream(outPath.toString()), StandardCharsets.UTF_8)) {
                            writer.write(sourceCode);
                            writer.flush();
                        }

                        StaticUtil.showWarningNotification("Template Generate", templatePath + " Generate Success", project, NotificationType.INFORMATION);

                    } catch (StopException _ignore) {
//                        StaticUtil.showWarningNotification("Template Generate", entry.getKey() + " Stop process", project, NotificationType.WARNING);
                    } catch (Exception e) {
                        Messages.showErrorDialog("Template: " + templatePath + "\n" + e.getMessage() + "\n" + StaticUtil.extractFullStack(e), "Error");
                        hasCloseDialog.set(false);
                    }
                });
            });

            if (hasCloseDialog.get()) {
                super.doOKAction();
            }

        } catch (Exception e) {

            Messages.showErrorDialog(e.getMessage() + "\n" + StaticUtil.extractFullStack(e), "Error");
        }
    }

    private void refreshTemplateGroupSelect() {
        templateGroupSelected.removeAllItems();

        var globalHistoryState = HistoryStateService.getInstance().getState();

        globalHistoryState.getHistoryUsePath().stream().sorted().forEachOrdered(input -> templateGroupSelected.insertItemAt(input.getMember(), 0));

        if (templateGroupSelected.getItemCount() > 0) {
            templateGroupSelected.setSelectedIndex(0);
        }
    }

    /**
     * 选择要生成的模板
     */
    private void refreshGenerateTemplatePanel() {
        var templatePath = scopeState.getTemplateGroupPath();

        if (templatePath == null) {
            return;
        }

        var globalHistoryState = HistoryStateService.getInstance().getState();
        var dir = Path.of(templatePath);
        var file = dir.toFile();
        if (!file.exists()) {
            globalHistoryState.getHistoryUsePath().remove(new ScoredMember(templatePath));
            return;
        }
        var templateFiles = FileUtil.fileTraverser(file).toList();

        if (!templatePath.isBlank()) {
            globalHistoryState.getHistoryUsePath().add(new ScoredMember(templatePath));
        }

        var paths = new HashMap<String, Path>();

        var fileNames = templateFiles.stream().filter(File::isFile)
                .peek(x -> paths.put(StaticUtil.extractDirAfterName(x.getPath(), dir.toString()), x.toPath()))
                .map(x -> StaticUtil.extractDirAfterName(x.getPath(), dir.toString()))
                .collect(Collectors.toList());

        var tables = new ListCheckboxComponent(new DoubleColumnLayout(), fileNames);

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
//            Objects.requireNonNull(scopeState.getTemplateGroup()).setSelectedItem(templatePath);

    }

    private void initDatabaseTreeState() {
        //获取选中的所有表
        // 获取当前项目所有数据源
        var allTables = new HashMap<String, DasTable>(16);

        var dbTables = DasUtil.extractTableFromDatabase(event.getDataContext());
        var selectTables = DasUtil.extractSelectTablesFromPsiElement(event.getDataContext()).map(DasNamed::getName).collect(Collectors.toSet());

        dbTables.forEach(x -> allTables.put(x.getName(), x));

        if (allTables.isEmpty()) {
            return;
        }

        var model = new DefaultListModel<String>();
        for (var table : allTables.keySet()) {
            model.addElement(table);
        }

        var tables = new ListCheckboxComponent(new SingleColumnLayout(), allTables.values().stream().sorted((a, b) -> {
            if (a.getDasParent() == null) {
                return 1;
            }
            if (b.getDasParent() == null) {
                return -1;
            }
            var i = b.getDasParent().getName().compareTo(a.getDasParent().getName());
            return i == 0 ? b.getName().compareTo(a.getName()) : i;
        }).map(DasNamed::getName).toList());

        Objects.requireNonNull(tables.getCheckBoxList()).forEach(x -> {
            if (selectTables.contains(x.getText())) {
                x.setSelected(true);
            }
        });
        if (tables.getCheckBoxList().stream().anyMatch(jbCheckBox -> !jbCheckBox.isSelected())) {
            tableSelectButton.setText("Select All");
        } else {
            tableSelectButton.setText("Clear All");
        }

        tableSelectButton.addActionListener(e -> {
            if (tables.getCheckBoxList().stream().anyMatch(jbCheckBox -> !jbCheckBox.isSelected())) {
                tableSelectButton.setText("Clear All");
                tables.getCheckBoxList().forEach(x -> x.setSelected(true));
            } else {
                tableSelectButton.setText("Select All");
                tables.getCheckBoxList().forEach(x -> x.setSelected(false));
            }
        });

        tableScrollPanel.setViewportView(tables);

        scopeState.setAllTableAndComponent(allTables, tables);

    }

    private void createUIComponents() {
        templateGroupSelected = new SelectionHistoryComboBox(new IHistorySelectedDelegate<>() {

            @Override
            public void selectItem(String item) {
                var instance = HistoryStateService.getInstance();
                var state = instance.getState();
                for (var member : state.getHistoryUsePath()) {
                    if (member.getMember().equals(item)) {
                        member.setScore(System.currentTimeMillis());
                        instance.loadState(state);
                        break;
                    }
                }
            }

            @Override
            public @Nullable String getSelectItem() {
                var state = HistoryStateService.getInstance().getState();
                return state.getHistoryUsePath().stream()
                        .peek(x -> {
                            if (x.getScore() == null) x.setScore(0L);
                        })
                        .max(Comparator.comparingLong(ScoredMember::getScore))
                        .map(ScoredMember::getMember)
                        .orElse(null);
            }

            @Override
            public @NotNull Collection<String> getSelectList() {
                var state = HistoryStateService.getInstance().getState();
                return state.getHistoryUsePath().stream()
                        .filter(x -> x.getMember() != null)
                        .map(x -> String.valueOf(x.getMember()))
                        .toList();
            }
        });
        templateGroupSelected.setEditable(true);
        templateGroupSelected.addItemListener(e -> refreshGenerateTemplatePanel());
        typeMappingSelected = new SelectionHistoryComboBox(new IHistorySelectedDelegate<>() {
            @Override
            public void selectItem(String item) {
                var state = HistoryStateService.getInstance().getState();
                state.setHistoryUseTypeMapper(item);
            }

            @Override
            public @NotNull Collection<String> getSelectList() {
                var globalState = GlobalStateService.getInstance().getState();
                return globalState.getTypeMappingGroupMap().keySet();
            }

            @Override
            public @Nullable String getSelectItem() {
                var state = HistoryStateService.getInstance().getState();
                return state.getHistoryUseTypeMapper();
            }
        });
    }

    private void updateNamespaceTextInput() {
        if (namespaceLockCheckBox.isSelected()) {
            return;
        }
        var basePath = project.getBasePath();
        if (basePath == null) {
            basePath = "";
        }

        var namespace = "";

        if (pathInput.getText().startsWith(basePath)) {
            namespace = pathInput.getText().substring(basePath.length());
        }
        namespace = namespace.startsWith("/") || namespace.startsWith("\\") ? namespace.substring(1) : namespace;

        if (namespace.isBlank()) {
            namespace = project.getName();
        } else {
            var replacement = namespaceSplitChar.getSelectedItem() == null ? "." : namespaceSplitChar.getSelectedItem().toString();
             namespace = StringUtil.replace(namespace, "\\", replacement);
             namespace = StringUtil.replace(namespace, "/", replacement);
        }
        namespaceTextField.setText(namespace);
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

        templateChoose.addActionListener(e -> fileChooserConsumer.apply(e).ifPresent(x -> scopeState.setTemplateGroupPath(x.getPath())));

        //region output path init
        pathInput.setText(Objects.requireNonNull(project).getBasePath());
        pathInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateNamespaceTextInput();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateNamespaceTextInput();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        //endregion

        //region init namespaceSplitChar
        var namespaceSplitCharList = GlobalStateService.getInstance().getState().getNamespaceSplitChar();
        namespaceSplitChar.setEditable(true);
        for (var i = 0; i < namespaceSplitCharList.size(); i++) {
            var item = namespaceSplitCharList.get(i);
            namespaceSplitChar.addItem(item.getValue());
            if (item.isSelect()) {
                namespaceSplitChar.setSelectedIndex(i);
            }
        }

        namespaceSplitChar.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                var selectedObjects = e.getItem();
                for (ComboBoxSelect x : namespaceSplitCharList) {
                    x.setSelect(x.getValue() == selectedObjects);
                }
                updateNamespaceTextInput();
            }
        });
        //endregion

        //region namespace text field init
        namespaceTextField.setText(project.getName());
        namespaceTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                namespaceLockCheckBox.setSelected(true);
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        //endregion

        refreshTemplateGroupSelect();

        initDatabaseTreeState();

        refreshButton.addActionListener(e -> refreshGenerateTemplatePanel());

        init();
    }
}
