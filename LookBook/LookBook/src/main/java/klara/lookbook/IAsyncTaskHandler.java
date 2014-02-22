package klara.lookbook;

import android.content.Context;
import android.support.v4.app.FragmentManager;

public interface IAsyncTaskHandler {
    public abstract FragmentManager myGetFragmentManager();
    public abstract String myGetTag();
    public abstract Context myGetContext();
}
