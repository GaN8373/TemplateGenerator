package generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import generator.ui.dialog.GenerateConfigDialog;

public class TemplateGeneratorPluginsAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        var tableGenerator = new GenerateConfigDialog(e);
        tableGenerator.setTitle("TemplateGenerator");
        tableGenerator.setSize(800, 600);
        tableGenerator.pack();

        tableGenerator.setVisible(true);
        tableGenerator.setUndecorated(true);
    }
}
