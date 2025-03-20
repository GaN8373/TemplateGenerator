package generator.ui.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsContexts;
import generator.MapperAction;
import generator.config.GlobalState;
import generator.data.TableRowData;
import generator.data.TypeMapper;
import generator.interfaces.impl.GlobalStateService;
import generator.util.StaticUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class TypeMapperSettingView implements Configurable {
    private final GlobalState globalState;

    private JPanel panel1;
    private JComboBox<String> typeMappingSelect;
    private JScrollPane mappingScrollPanel;
    private JButton newButton;
    private JButton renameButton;
    private JButton copyButton;
    private JButton delButton;
    private JTable typeMappingTable;
    private JButton newRowButton;
    private JButton delRowButton;
    private JButton importButton;
    private JButton exportButton;

    @Override
    public @Nullable JComponent createComponent() {
        return this.panel1;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() {
        GlobalStateService.getInstance().loadState(globalState);
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "TemplateGeneratorSettings";
    }

    public TypeMapperSettingView() {
        this.globalState = GlobalStateService.getInstance().getState();

        initButton();

        initTypeMappingSelect();
        refreshTypeMappingTable();

    }

    private void initButton() {
        newButton.addActionListener(e -> {
            var item = ("TypeMappingGroup_" + System.currentTimeMillis());

            typeMappingSelect.addItem(item);
            typeMappingSelect.setSelectedItem(item);
            globalState.getGroupMapTemplate().put(item, new HashSet<>());
        });

        copyButton.addActionListener(e -> {
            var selectedItem = typeMappingSelect.getSelectedItem();
            if (selectedItem instanceof String label) {
                var newItem = label + "_copy";
                var typeMappers = globalState.getGroupMapTemplate().computeIfAbsent(label, k -> new HashSet<>());
                var collect = typeMappers.stream().map(x -> new TypeMapper(x.getAction(), x.getRule(), x.getType())).collect(Collectors.toSet());
                globalState.getGroupMapTemplate().put(newItem, collect);
                typeMappingSelect.addItem(newItem);
            }
        });
        renameButton.addActionListener(e -> {
            String newLabel = JOptionPane.showInputDialog(null, "New Name:", "Rename", JOptionPane.PLAIN_MESSAGE);
            if (newLabel != null && !newLabel.trim().isEmpty()) {
                var selectedItem = typeMappingSelect.getSelectedItem();
                if (selectedItem instanceof String label) {
                    var groupMapTemplate = globalState.getGroupMapTemplate();
                    var typeMappers = groupMapTemplate.computeIfAbsent(label, k -> new HashSet<>());

                    groupMapTemplate.put(newLabel, typeMappers);
                    groupMapTemplate.remove(label);
                    var itemIndex = typeMappingSelect.getSelectedIndex();
                    typeMappingSelect.removeItemAt(itemIndex);
                    typeMappingSelect.insertItemAt(newLabel, itemIndex);
                    typeMappingSelect.setSelectedItem(newLabel);
                }
            }
        });

        delButton.addActionListener(e -> {
            var selectedItem = typeMappingSelect.getSelectedItem();

            if (selectedItem instanceof String label) {
                globalState.getGroupMapTemplate().remove(label);
                typeMappingSelect.removeItemAt(typeMappingSelect.getSelectedIndex());
            }
        });

        importButton.addActionListener(e -> {

                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createMultipleFilesNoJarsDescriptor();
                descriptor.setForcedToUseIdeaFileChooser(true);
                descriptor.withFileFilter(virtualFile -> {
                    if (virtualFile.isDirectory()) {
                        return true;
                    }
                    return "json".equalsIgnoreCase(virtualFile.getExtension());
                });

                var virtualFile = FileChooser.chooseFiles(descriptor, ProjectManager.getInstance().getDefaultProject(), null);

                String lastTemplate = null;
                for (var file : virtualFile) {
                    try {
                        var typeMappers = StaticUtil.getJSON().readValue(file.getInputStream(), new TypeReference<HashSet<TypeMapper>>() {
                        });

                        var groupMapTemplate = globalState.getGroupMapTemplate();
                        if (!groupMapTemplate.containsKey(file.getName())) {
                            typeMappingSelect.insertItemAt(file.getName(), 0);
                        }
                        groupMapTemplate.put(file.getName(), typeMappers);
                        lastTemplate = file.getName();
                    } catch (IOException ex) {
                        StaticUtil.showWarningNotification("Json", file.getName() + "Json Deserialize Failed", ProjectManager.getInstance().getDefaultProject(), NotificationType.WARNING);
                    }
                }

                if (lastTemplate != null) {
                    typeMappingSelect.setSelectedIndex(0);
                }

        });

        exportButton.addActionListener(e -> {
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            descriptor.setForcedToUseIdeaFileChooser(true);

            var virtualFile = FileChooser.chooseFile(descriptor, ProjectManager.getInstance().getDefaultProject(), null);
            if (virtualFile == null) {
                return;
            }

            var selectedItem = typeMappingSelect.getSelectedItem();
            if (!(selectedItem instanceof String item)) {
                return;
            }

            var path = virtualFile.getPath();
            var typeMappers = globalState.getGroupMapTemplate().get(item);
            if (typeMappers == null) {
                return;
            }

            try {
                var file = Path.of(path, item + ".json").toFile();
                if (file.exists()) {
                    file = Path.of(path, "%s_%d.json".formatted(item, System.currentTimeMillis())).toFile();
                }

                var w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
                StaticUtil.getJSON().writeValue(w, typeMappers);
            } catch (IOException ex) {
                StaticUtil.showWarningNotification("Json", virtualFile.getName() + "Json Serialize Failed", ProjectManager.getInstance().getDefaultProject(), NotificationType.WARNING);
            }
        });
    }

    private void initTypeMappingSelect() {
        globalState.getGroupMapTemplate().keySet().forEach(item -> typeMappingSelect.addItem(item));
        typeMappingSelect.addItemListener(e -> refreshTypeMappingTable());
    }

    private void refreshTypeMappingTable() {
        TableRowData tableRowData;

        if (typeMappingSelect.getSelectedItem() instanceof String typeKey) {
            var typeMappers = globalState.getGroupMapTemplate().computeIfAbsent(typeKey, k -> new HashSet<>());
            tableRowData = new TableRowData(typeMappers.stream().sorted().toList());
        } else {
            tableRowData = new TableRowData(new ArrayList<>());
        }

        typeMappingTable.setModel(tableRowData);
        var defaultCellEditor = new DefaultCellEditor(new JComboBox<>(MapperAction.getEntries().stream().map(Enum::name).toArray()));

        typeMappingTable.getColumnModel().getColumn(0).setCellEditor(defaultCellEditor);

        for (var actionListener : newRowButton.getActionListeners()) {
            newRowButton.removeActionListener(actionListener);
        }
        newRowButton.addActionListener(e -> {
            if (typeMappingSelect.getSelectedItem() instanceof String str) {
                var typeMappers = globalState.getGroupMapTemplate().computeIfAbsent(str, k -> new HashSet<>());
                typeMappers.add(new TypeMapper(MapperAction.Eq, "", ""));
                refreshTypeMappingTable();
            }
        });

        for (var actionListener : delRowButton.getActionListeners()) {
            delRowButton.removeActionListener(actionListener);
        }
        delRowButton.addActionListener(e -> {
            if (typeMappingSelect.getSelectedItem() instanceof String str) {
                var typeMappers = globalState.getGroupMapTemplate().computeIfAbsent(str, k -> new HashSet<>());
                var selectedRow = typeMappingTable.getSelectedRow();
                if (selectedRow >= 0) {
                    typeMappers.remove(tableRowData.data.get(selectedRow));
                    refreshTypeMappingTable();
                }
            }
        });
    }

}
