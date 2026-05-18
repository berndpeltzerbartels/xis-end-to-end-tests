package app;

import one.xis.OwnedBy;
import one.xis.OwnershipGuard;
import one.xis.Roles;
import one.xis.UserContext;

@Roles("DATA_EDITOR")
@OwnedBy(EditorOwnershipGuard.class)
public record EditorForm(String documentId, String value) {
}

class EditorOwnershipGuard implements OwnershipGuard<EditorForm> {
    @Override
    public boolean mayAccess(EditorForm form, UserContext userContext) {
        return "editor".equals(userContext.getUserId()) && "article-editor".equals(form.documentId());
    }
}
