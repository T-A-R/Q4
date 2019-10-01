package pro.quizer.quizer3.view.fragment;

public interface IMainFragment {
    void showScreensaver(String title, boolean full);
    void hideScreensaver();
    void showMenu();
    void hideMenu();
    void showSideMenuDrawer();
    void hideSideMenuDrawer();
    void setMenuCursor(int index);
}
