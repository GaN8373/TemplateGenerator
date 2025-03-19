package generator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import generator.ui.dialog.GenerateConfigDialog;
import k.O.EX;

public class TemplateGeneratorPluginsAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        var tableGenerator = new GenerateConfigDialog(e);

        tableGenerator.setTitle("TemplateGenerator");
        tableGenerator.setSize(800, 600);

        tableGenerator.show();
    }
}
