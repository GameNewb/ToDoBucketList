package edu.sjsu.kyle.todobucketlist;

/**
 * Created by Kiyeon on 12/1/2017.
 */

public class IntentConstants
{
    // Google Sign On and Profile Constants
    public final static String INTENT_GOOGLE_API = "googleAPI";
    public final static int INTENT_GOOGLE_REQUEST_CODE = 900;
    public final static int INTENT_GOOGLE_REQUEST_CODE_SIGNOUT = 901;
    public final static String INTENT_SIGNIN_NAME = "signInName";
    public final static String INTENT_SIGNIN_EMAIL = "signInEmail";
    public final static String INTENT_SIGNIN_PHOTO = "signInPhoto";

    // To-Do list constants
    public final static int INTENT_REQUEST_CODE = 1;
    public final static int INTENT_RESULT_CODE = 1;
    public final static int INTENT_REQUEST_CODE_TWO = 2;
    public final static int INTENT_RESULT_CODE_TWO = 2;
    public final static String INTENT_ADD_ITEM = "addItem";
    public final static String INTENT_LIST_DATA = "listData";
    public final static String INTENT_ITEM_POSITION = "itemPosition";
    public final static String INTENT_EDIT_ITEM = "editItem";
    public final static String INTENT_TO_DO_LIST = "toDoList";
}
