package in.dragons.galaxy.task.playstore;

import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.IteratorGooglePlayException;

import java.io.IOException;

import in.dragons.galaxy.PlayStoreApiAuthenticator;

abstract public class PlayStorePayloadTask<T> extends PlayStoreTask<T> {

    abstract protected T getResult(GooglePlayAPI api, String... arguments) throws IOException;

    @Override
    protected T doInBackground(String... arguments) {
        try {
            return getResult(new PlayStoreApiAuthenticator(context).getApi(), arguments);
        } catch (IOException e) {
            exception = e;
        } catch (IteratorGooglePlayException e) {
            exception = e.getCause();
        }
        return null;
    }
}
