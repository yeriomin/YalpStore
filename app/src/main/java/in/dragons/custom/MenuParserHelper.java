package in.dragons.custom;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.List;

/**
 * Created by Valentin on 14/06/2017.
 */

public class MenuParserHelper {

    public static void parseMenu(Context context, @MenuRes int menuRes, List<MenuEntry> menuEntryList){
        PopupMenu p = new PopupMenu(context, null);
        Menu menu = p.getMenu();
        new MenuInflater(context).inflate(menuRes, menu);

        for (int i = 0; i < menu.size(); i++) {
            android.view.MenuItem item = menu.getItem(i);
            menuEntryList.add(new MenuEntry(item.getTitle().toString(), item.getIcon(), item.getItemId()));
        }
    }
}
