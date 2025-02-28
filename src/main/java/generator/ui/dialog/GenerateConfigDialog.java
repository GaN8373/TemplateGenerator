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
import generator.config.ScopeState;
import generator.interfaces.GlobalStateService;
import generator.interfaces.impl.layout.DoubleColumnLayout;
import generator.interfaces.impl.layout.SingleColumnLayout;
import generator.ui.components.ListCheckboxComponent;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
        scopeState = ScopeState.newInstance(project, pathInput, (JTextField)templateGroupSelected.getEditor().getEditorComponent());
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

        chooseButton.addActionListener(e -> fileChooserConsumer.apply(e).ifPresent(x-> scopeState.setGenerateFileStorePath(x.getPath())));
        templateChoose.addActionListener(e-> fileChooserConsumer.apply(e).ifPresent(x-> scopeState.setTemplateGroupPath(x.getPath())));

        templateGroupSelected.setEditable(true);
        initDatabaseTreeState();

        initTypeMappingSelect();

        refreshGenerateTemplatePanel();
        refreshButton.addActionListener(e -> refreshGenerateTemplatePanel());
    }

    private void initTypeMappingSelect() {
        var service = ApplicationManager.getApplication().getService(GlobalStateService.class);
        var globalState = service.getState();

        globalState.getGroupMapTemplate().forEach((k, v) -> typeMappingSelected.addItem(k));
        typeMappingSelected.addActionListener(e -> {
            var selectedItem = templateGroupSelected.getSelectedItem();
            if (selectedItem instanceof String label) {
                scopeState.setSelectTypeMapping(label);
            } else if (selectedItem instanceof JLabel textField) {
                scopeState.setSelectTypeMapping(textField.getText());
            } else if (selectedItem instanceof JTextField textField) {
                scopeState.setSelectTypeMapping(textField.getText());
            }
        });
    }

    private void refreshGenerateTemplatePanel() {
        var templatePath = scopeState.getTemplateGroupPath();

        if (templatePath == null) {
            return;
        }

        var dir = Path.of(templatePath);

        try (var templateGroup = Files.list(dir)) {
            var collect = templateGroup.map(x -> x.getFileName().toString()).collect(Collectors.toList());

            var tables = new ListCheckboxComponent(new DoubleColumnLayout(),collect);
            // TODO repeat add
            selectAllButton.addActionListener(e -> tables.getCheckBoxList().forEach(x -> x.setSelected(true)));
            clearSelectAllButton.addActionListener(e -> tables.getCheckBoxList().forEach(x -> x.setSelected(false)));
            generateTemplatePanel.setViewportView(tables);
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

//        databaseTree.setModel(new TreeModel(rootNode));
        scopeState.setAllTables(allTables);

    }

    private void onOK() {
        // 在此处添加您的代码
        dispose();
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }
}
