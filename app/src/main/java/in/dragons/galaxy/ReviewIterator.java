package in.dragons.galaxy;

import android.content.Context;

import in.dragons.galaxy.model.Review;

import java.util.Iterator;
import java.util.List;

abstract public class ReviewIterator implements Iterator<List<Review>> {

    protected String packageName;
    protected Context context;

    protected int page = -1;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
