package generator.ui.view;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import generator.config.GlobalState;
import generator.data.TypeMapper;
import generator.interfaces.GlobalStateService;
import generator.interfaces.impl.listener.TypeMappingTableMouseListener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    @Override
    public @Nullable JComponent createComponent() {
        return this.panel1;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        var service = ApplicationManager.getApplication().getService(GlobalStateService.class);
        service.loadState(globalState);
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "TemplateGeneratorSettings";
    }

    public TypeMapperSettingView() {
        var service = ApplicationManager.getApplication().getService(GlobalStateService.class);

        this.globalState = service.getState();

        initButton();

        initTypeMappingSelect();
        refreshTypeMappingTable(typeMappingSelect);
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
                    typeMappingSelect.insertItemAt(newLabel,itemIndex);
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
    }

    private void initTypeMappingSelect() {
        globalState.getGroupMapTemplate().keySet().forEach(item -> typeMappingSelect.addItem(item));
        typeMappingSelect.addItemListener(e-> refreshTypeMappingTable(typeMappingSelect));
    }

    private void refreshTypeMappingTable(JComboBox<String> typeMappingSelect) {

        String[] columnNames = {"Action", "Rule", "Write"};

        var defaultTableModel = new DefaultTableModel(columnNames, 0);

        if (typeMappingSelect.getSelectedItem() instanceof String typeKey) {
            var typeMappers = globalState.getGroupMapTemplate().computeIfAbsent(typeKey, k -> new HashSet<>());
            typeMappers.stream().sorted().forEachOrdered(x-> defaultTableModel.addRow(new Object[]{x.getAction().name(), x.getRule(), x.getType()}));
        }

        typeMappingTable.setModel(defaultTableModel);
        for (var mouseListener : typeMappingTable.getMouseListeners()) {
            if (mouseListener instanceof TypeMappingTableMouseListener) {
                typeMappingTable.removeMouseListener(mouseListener);
            }
        }
        typeMappingTable.addMouseListener(new TypeMappingTableMouseListener(typeMappingTable));
    }

}
