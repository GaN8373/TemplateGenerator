package generator.ui.dialog;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import freemarker.template.Template;
import generator.config.ScopeState;
import generator.data.TypeMapper;
import generator.data.table.TableData;
import generator.interfaces.GlobalStateService;
import generator.interfaces.impl.layout.DoubleColumnLayout;
import generator.interfaces.impl.layout.SingleColumnLayout;
import generator.ui.components.ListCheckboxComponent;
import generator.util.NameUtil;
import generator.util.TemplateUtil;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenerateConfigDialog extends JDialog {
    private final ScopeState scopeState;
    private final Project project;
    private final AnActionEvent event;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
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


    public GenerateConfigDialog(AnActionEvent event) {
        this.event = event;
        this.project = event.getProject();
        scopeState = new ScopeState(project, pathInput, (JTextField) templateGroupSelected.getEditor().getEditorComponent(), typeMappingSelected);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        Function<ActionEvent, Optional<VirtualFile>> fileChooserConsumer = e -> {
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            descriptor.setTitle("选择路径");
            // 2. 弹出路径选择器
            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            return Optional.ofNullable(virtualFile);
        };

        chooseButton.addActionListener(e -> fileChooserConsumer.apply(e).ifPresent(x -> scopeState.setGenerateFileStorePath(x.getPath())));
        templateChoose.addActionListener(e -> fileChooserConsumer.apply(e).ifPresent(x -> scopeState.setTemplateGroupPath(x.getPath())));

        refreshTemplateGroupSelect();

        initDatabaseTreeState();

        initTypeMappingSelect();

        refreshGenerateTemplatePanel();
        refreshButton.addActionListener(e -> refreshGenerateTemplatePanel());

    }

    private void refreshTemplateGroupSelect() {
        templateGroupSelected.setEditable(true);

        templateGroupSelected.removeAllItems();

        var service = ApplicationManager.getApplication().getService(GlobalStateService.class);
        var globalState = service.getState();
        for (var i = globalState.getHistoryUsePath().size() - 1; i >= 0; i--) {
            templateGroupSelected.addItem(globalState.getHistoryUsePath().get(i));
        }
    }

    private void initTypeMappingSelect() {
        var service = ApplicationManager.getApplication().getService(GlobalStateService.class);
        var globalState = service.getState();

        globalState.getGroupMapTemplate().forEach((k, v) -> typeMappingSelected.addItem(k));
    }

    private void refreshGenerateTemplatePanel() {
        var templatePath = scopeState.getTemplateGroupPath();

        if (templatePath == null) {
            return;
        }

        var dir = Path.of(templatePath);

        try (var templateGroup = Files.list(dir)) {
            if (!templatePath.isBlank()) {
                var service = ApplicationManager.getApplication().getService(GlobalStateService.class);
                var globalState = service.getState();
                globalState.getHistoryUsePath().add(templatePath);
                templateGroupSelected.insertItemAt(templatePath, 0);
            }

            var paths = new HashMap<String, Path>();

            var collect = templateGroup.peek(x -> paths.put(x.getFileName().toString(), x)).map(x -> x.getFileName().toString()).collect(Collectors.toList());

            var tables = new ListCheckboxComponent(new DoubleColumnLayout(), collect);

            for (var actionListener : selectAllButton.getActionListeners()) {
                selectAllButton.removeActionListener(actionListener);
            }
            selectAllButton.addActionListener(e -> tables.getCheckBoxList().forEach(x -> x.setSelected(true)));

            for (var actionListener : clearSelectAllButton.getActionListeners()) {
                clearSelectAllButton.removeActionListener(actionListener);
            }
            clearSelectAllButton.addActionListener(e -> tables.getCheckBoxList().forEach(x -> x.setSelected(false)));
            generateTemplatePanel.setViewportView(tables);
            scopeState.setTemplateFilePath(paths, tables);
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

    private void onOK() {
        var service = ApplicationManager.getApplication().getService(GlobalStateService.class);
        var globalState = service.getState();

        var fileNameMapTemplate = new HashMap<String, String>();
        try {
            for (var path : scopeState.getSelectedTemplatePath()) {
                var strings = Files.readString(path);

                fileNameMapTemplate.put(path.getFileName().toString().split("\\.*")[0], strings);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var mapperTemplates = globalState.getTemplates(scopeState.getSelectTypeMapping());
        if (mapperTemplates.isEmpty()) {
            JOptionPane.showMessageDialog(null, "TypeMapper is not empty","Message！",  JOptionPane.WARNING_MESSAGE);
            return;
        }

        scopeState.getSelectedTables().parallelStream().map(x -> new TableData(x, mapperTemplates)).forEach(tableData -> {
            for (var entry : fileNameMapTemplate.entrySet()) {
                Template template;
                try {
                    template = new Template(entry.getKey(), entry.getValue(), TemplateUtil.cfg);
                    var root = new HashMap<String, Object>();
                    root.put("table", tableData.getDbTable());
                    root.put("columns", tableData.getColumns());
                    root.put("NameUtil", new NameUtil());

                    template.process(root, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 在此处添加您的代码
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }
}
