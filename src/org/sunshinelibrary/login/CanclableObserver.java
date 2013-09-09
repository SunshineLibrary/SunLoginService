package org.sunshinelibrary.login;

/**
 * Created with IntelliJ IDEA.
 * User: solomon
 * Date: 13-7-31
 * Time: 下午6:25
 */

public interface CanclableObserver {

    public void dismissDialog(String situation);
    public void displayLoginWindow();
}