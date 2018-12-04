package smg.interfaces;

public interface ItemsAdapterInterface {
    // calls method in Activity from corresponding Adapter
    void refreshToolbar(boolean deleteButtonVisible, boolean editButtonVisible);
    void sort();
}
