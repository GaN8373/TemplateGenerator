package generator;

import com.intellij.database.model.ObjectKind;
import com.intellij.openapi.actionSystem.*;
import generator.ui.dialog.GenerateConfigDialog;
import generator.util.DasUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TemplateGeneratorPluginsAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    private static final Set<ObjectKind> SUPPORTED_KINDS = Set.of(
            ObjectKind.TABLE,
            ObjectKind.VIEW,
            ObjectKind.SCHEMA,
            ObjectKind.DATABASE
    );
    @Override
    public void update(@NotNull AnActionEvent e) {
        var dasObjectStream = DasUtil.extractDatabaseDas(e.getDataContext());

        var b = dasObjectStream.anyMatch(x -> SUPPORTED_KINDS.contains(x.getKind()));
        e.getPresentation().setEnabledAndVisible(b);
        super.update(e);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        var tableGenerator = new GenerateConfigDialog(e);

        tableGenerator.setTitle("TemplateGenerator");
        tableGenerator.setSize(800, 600);

        tableGenerator.show();
    }
}
