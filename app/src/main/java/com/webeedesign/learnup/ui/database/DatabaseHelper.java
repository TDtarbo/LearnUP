package com.webeedesign.learnup.ui.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.webeedesign.learnup.ui.CurrentUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DatabaseHelper extends SQLiteOpenHelper {


    //Initialize DB, table and Columns
    public static final String DATABASE_NAME = "LearnUP.db";
    public static final String TABLE_TASKS_NAME = "tasks";
    public static final String TASK_COL_1 = "ID";
    public static final String TASK_COL_2 = "CELL";
    public static final String TASK_COL_3 = "CATEGORY";
    public static final String TASK_COL_4 = "TITLE";
    public static final String TASK_COL_5 = "DESCRIPTION";
    public static final String TASK_COL_6 = "DATE";
    public static final String TASK_COL_7 = "TIME";
    public static final String TASK_COL_8 = "LOCATION";
    public static final String TASK_COL_9 = "WEB_LINK";
    public static final String TASK_COL_10 = "WEEK";
    public static final String TASK_COL_11 = "USER_ID";

    public static final String TASK_COL_12 = "IS_CHECKED";

    public static final String TODO_LISTS_TABLE_NAME = "todo_lists";
    public static final String TODO_LISTS_COL_1 = "ID";
    public static final String TODO_LISTS_COL_2 = "LIST_NAME";
    public static final String TODO_LISTS_COL_3 = "DATE_CREATED";
    public static final String TODO_LISTS_COL_4 = "USER_ID";


    public static final String TODOS_TABLE_NAME = "todos";
    public static final String TODOS_COL_1 = "ID";
    public static final String TODOS_COL_2 = "TODO_TITLE";
    public static final String TODOS_COL_3 = "LIST_ID";
    public static final String TODOS_COL_4 = "IS_CHECKED";
    public static final String TODOS_COL_5 = "USER_ID";

    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_COL_1 = "ID";
    public static final String USERS_COL_2 = "USER_NAME";
    public static final String USERS_COL_3 = "PASSWORD";
    public static final String USERS_COL_4 = "GENDER";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        //create tables
        sqLiteDatabase.execSQL("CREATE TABLE "+ TABLE_TASKS_NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CELL TEXT," +
                "CATEGORY INTEGER," +
                "TITLE TEXT, " +
                "DESCRIPTION TEXT, " +
                "DATE TEXT, " +
                "TIME TEXT, " +
                "LOCATION TEXT, " +
                "WEB_LINK TEXT, " +
                "WEEK INTEGER," +
                "USER_ID INTEGER,"+
                "IS_CHECKED)");

        sqLiteDatabase.execSQL("CREATE TABLE "+ TODO_LISTS_TABLE_NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "LIST_NAME TEXT," +
                "DATE_CREATED TEXT," +
                "USER_ID INTEGER)");

        sqLiteDatabase.execSQL("CREATE TABLE "+ TODOS_TABLE_NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TODO_TITLE TEXT," +
                "LIST_ID INTEGER," +
                "IS_CHECKED INTEGER," +
                "USER_ID INTEGER)");

        sqLiteDatabase.execSQL("CREATE TABLE "+ USERS_TABLE_NAME + "(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_NAME TEXT," +
                "PASSWORD TEXT," +
                "GENDER TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_TASKS_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TODO_LISTS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TODOS_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ USERS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    //Insert data to Task table
    public boolean insertDataToTaskTable(
            String cell,
            int category,
            String title,
            String description,
            String date,
            String time,
            String location,
            String link,
            int week) throws ParseException {



        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_COL_2, cell);
        contentValues.put(TASK_COL_3, category);
        contentValues.put(TASK_COL_4, title);
        contentValues.put(TASK_COL_5, description);
        contentValues.put(TASK_COL_6, date);
        contentValues.put(TASK_COL_7, time);
        contentValues.put(TASK_COL_8, location);
        contentValues.put(TASK_COL_9, link);
        contentValues.put(TASK_COL_10, week);
        contentValues.put(TASK_COL_11, CurrentUser.getInstance().getUserId());
        contentValues.put(TASK_COL_12, "0");
        long results = sqLiteDatabase.insert(TABLE_TASKS_NAME,null,contentValues);

        return results != -1;
    }

    //Fetch data for weekly calender
    @SuppressLint("Range")
    public static LinkedHashMap<Integer, String[]> getDataForNextEightDays(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get the date range for the next eight days, including today
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 7);
        String nextEightDays = dateFormat.format(calendar.getTime());

        String query = "SELECT * FROM tasks WHERE USER_ID = ? AND DATE >= ? AND DATE <= ? ORDER BY DATE ASC, TIME ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(CurrentUser.getInstance().getUserId()), today, nextEightDays});
        LinkedHashMap<Integer, String[]> dataHashMap = new LinkedHashMap<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(TASK_COL_1));
            String[] data = new String[10];
            // Populate the String array with values as needed
            data[0] = cursor.getString(cursor.getColumnIndex(TASK_COL_2));
            data[1] = cursor.getString(cursor.getColumnIndex(TASK_COL_3));
            data[2] = cursor.getString(cursor.getColumnIndex(TASK_COL_4));
            data[3] = cursor.getString(cursor.getColumnIndex(TASK_COL_5));
            data[4] = cursor.getString(cursor.getColumnIndex(TASK_COL_6));
            data[5] = cursor.getString(cursor.getColumnIndex(TASK_COL_7));
            data[6] = cursor.getString(cursor.getColumnIndex(TASK_COL_8));
            data[7] = cursor.getString(cursor.getColumnIndex(TASK_COL_9));
            data[8] = cursor.getString(cursor.getColumnIndex(TASK_COL_10));
            data[9] = cursor.getString(cursor.getColumnIndex(TASK_COL_12));
            dataHashMap.put(id, data);
        }

        cursor.close();
        db.close();
        return dataHashMap;
    }



    //Fetch data for weekly calender
    public static HashMap<String, Integer> getDataForWeek(int CurrentWeekNumber, Context context){

        DatabaseHelper dbHelper = new DatabaseHelper(context); // create an instance of the DatabaseHelper class
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT CELL, CATEGORY FROM tasks WHERE WEEK = ? AND USER_ID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(CurrentWeekNumber), String.valueOf(CurrentUser.getInstance().getUserId())});
        HashMap<String, Integer> data = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                int cell = cursor.getInt(1);
                data.put(category, cell);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    //get data for monthly calender
    @SuppressLint("Range")
    public static LinkedHashMap<Integer, String[]> getDataForMonth(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM tasks WHERE USER_ID = ? ORDER BY DATE ASC, TIME ASC"; // Order by "DATE" and "TIME" columns in descending order
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(CurrentUser.getInstance().getUserId())}); // Use query variable in rawQuery method
        LinkedHashMap<Integer, String[]> dataHashMap = new LinkedHashMap<>(); // Use LinkedHashMap instead of HashMap
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(TASK_COL_1));
            String[] data = new String[9]; // Initialize with empty array of length 9
            // Populate the String array with values as needed
            data[0] = cursor.getString(cursor.getColumnIndex(TASK_COL_2));
            data[1] = cursor.getString(cursor.getColumnIndex(TASK_COL_3));
            data[2] = cursor.getString(cursor.getColumnIndex(TASK_COL_4));
            data[3] = cursor.getString(cursor.getColumnIndex(TASK_COL_5));
            data[4] = cursor.getString(cursor.getColumnIndex(TASK_COL_6));
            data[5] = cursor.getString(cursor.getColumnIndex(TASK_COL_7));
            data[6] = cursor.getString(cursor.getColumnIndex(TASK_COL_8));
            data[7] = cursor.getString(cursor.getColumnIndex(TASK_COL_9));
            data[8] = cursor.getString(cursor.getColumnIndex(TASK_COL_10));
            dataHashMap.put(id, data);
        }
        cursor.close();
        db.close();
        return dataHashMap;
    }


    //Update data from of the Tasks table
    public boolean updateTaskData(
            int id,
            String cell,
            int category,
            String title,
            String description,
            String date,
            String time,
            String location,
            String link,
            int week){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_COL_2, cell);
        contentValues.put(TASK_COL_3, category);
        contentValues.put(TASK_COL_4, title);
        contentValues.put(TASK_COL_5, description);
        contentValues.put(TASK_COL_6, date);
        contentValues.put(TASK_COL_7, time);
        contentValues.put(TASK_COL_8, location);
        contentValues.put(TASK_COL_9, link);
        contentValues.put(TASK_COL_10, week);
        sqLiteDatabase.update(TABLE_TASKS_NAME, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return true;

    }


    //Delete Task data from the database
    public Integer deleteTaskTableData(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Integer delete_data = sqLiteDatabase.delete(TABLE_TASKS_NAME,"ID = ? ", new String[] {id});
        sqLiteDatabase.close();
        return delete_data;
    }


    //Insert data to To Do list table
    public boolean insertIntoTodoLists(String listName, String date) throws ParseException {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_LISTS_COL_2, listName);
        contentValues.put(TODO_LISTS_COL_3, date);
        contentValues.put(TODO_LISTS_COL_4, CurrentUser.getInstance().getUserId());
        long results = sqLiteDatabase.insert(TODO_LISTS_TABLE_NAME,null,contentValues);

        return results != -1;
    }

    //get ToDo lists
    @SuppressLint("Range")
    public static LinkedHashMap<Integer, String[]> getTodoLists(Context context){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM todo_lists WHERE USER_ID = ? ORDER BY DATE_CREATED ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(CurrentUser.getInstance().getUserId())});
        LinkedHashMap<Integer, String[]> list = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(TODO_LISTS_COL_1));
            String[] data = new String[2];
            data[0] = cursor.getString(cursor.getColumnIndex(TODO_LISTS_COL_2));
            data[1] = cursor.getString(cursor.getColumnIndex(TODO_LISTS_COL_3));
            list.put(id, data);
        }
        cursor.close();
        return list;
    }


    //inset data in todos table
    public boolean insertIntoTodos(String itemName, int listId, int isChecked) throws ParseException {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODOS_COL_2, itemName);
        contentValues.put(TODOS_COL_3, listId);
        contentValues.put(TODOS_COL_4, isChecked);
        contentValues.put(TODOS_COL_5, CurrentUser.getInstance().getUserId());
        long results = sqLiteDatabase.insert(TODOS_TABLE_NAME,null,contentValues);

        return results != -1;
    }

    //get the todo items from todos table
    @SuppressLint("Range")
    public static LinkedHashMap<Integer, String[]> getTodoItems(Context context, int listId){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM todos WHERE LIST_ID = ? AND USER_ID = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(listId), String.valueOf(CurrentUser.getInstance().getUserId())});
        LinkedHashMap<Integer, String[]> list = new LinkedHashMap<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(TODOS_COL_1));
            String[] data = new String[2];
            data[0] = cursor.getString(cursor.getColumnIndex(TODOS_COL_2));
            data[1] = cursor.getString(cursor.getColumnIndex(TODOS_COL_4));
            list.put(id, data);
        }
        cursor.close();
        return list;
    }

    //get progress bar information
    public static int[] getProgressInfo(Context context, int listId){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to get the count of all todos for the given listId
        String queryOne = "SELECT COUNT(*) FROM todos WHERE LIST_ID = ? AND USER_ID = ?";
        Cursor cursorOne = db.rawQuery(queryOne, new String[]{String.valueOf(listId), String.valueOf(CurrentUser.getInstance().getUserId())});
        int totalCount = 0;
        if (cursorOne.moveToFirst()) {
            totalCount = cursorOne.getInt(0);
        }
        cursorOne.close();

        // Query to get the count of todos with id = 1 and check = 1
        String queryTwo = "SELECT COUNT(*) FROM todos WHERE LIST_ID = ? AND USER_ID = ? AND IS_CHECKED = 1";
        Cursor cursorTwo = db.rawQuery(queryTwo, new String[]{String.valueOf(listId), String.valueOf(CurrentUser.getInstance().getUserId())});
        int completedCount = 0;
        if (cursorTwo.moveToFirst()) {
            completedCount = cursorTwo.getInt(0);
        }
        cursorTwo.close();

        // Store the counts in an array
        int[] count = new int[]{totalCount, completedCount};
        return count;
    }


    //Update checkbox state
    public boolean updateCheckBox(int id, int isChecked){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODOS_COL_4, isChecked);
        sqLiteDatabase.update(TODOS_TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return true;

    }

    //Login validation
    @SuppressLint("Range")
    public int isUserValid(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE USER_NAME=? AND PASSWORD=?", new String[]{username, password});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex("ID"));
        }
        cursor.close();
        db.close();
        return userId;
    }


    // Sign up validation to check whether the entered user name is already in use
    public boolean isUserNameExist(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE USER_NAME=?", new String[]{username});

        boolean result = false;
        if (cursor.moveToFirst()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;

    }


    //Create new user account
    public boolean insertIntoUsers(String username, String password, String gender) throws ParseException {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COL_2, username);
        contentValues.put(USERS_COL_3, password);
        contentValues.put(USERS_COL_4, gender);
        long results = sqLiteDatabase.insert(USERS_TABLE_NAME,null,contentValues);

        return results != -1;
    }

    //get todo info from the task table for current user
    public static int[] getProfileTodoInfo(Context context){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to get the count of all todos for the given listId
        String queryOne = "SELECT COUNT(*) FROM todos WHERE USER_ID = ?";
        Cursor cursorOne = db.rawQuery(queryOne, new String[]{String.valueOf(CurrentUser.getInstance().getUserId())});
        int totalCount = 0;
        if (cursorOne.moveToFirst()) {
            totalCount = cursorOne.getInt(0);
        }
        cursorOne.close();

        // Query to get the count of todos with id = 1 and check = 1
        String queryTwo = "SELECT COUNT(*) FROM todos WHERE USER_ID = ? AND IS_CHECKED = 1";
        Cursor cursorTwo = db.rawQuery(queryTwo, new String[]{String.valueOf(CurrentUser.getInstance().getUserId())});
        int completedCount = 0;
        if (cursorTwo.moveToFirst()) {
            completedCount = cursorTwo.getInt(0);
        }
        cursorTwo.close();

        // Store the counts in an array
        int[] count = new int[]{totalCount, completedCount};
        return count;
    }

    //get task info from the task table for current user
    public static int getProfileTaskInfo(Context context){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to get the count of all todos for the given listId
        String queryOne = "SELECT COUNT(*) FROM tasks WHERE USER_ID = ?";
        Cursor cursor = db.rawQuery(queryOne, new String[]{String.valueOf(CurrentUser.getInstance().getUserId())});
        int totalCount = 0;
        if (cursor.moveToFirst()) {
            totalCount = cursor.getInt(0);
        }
        cursor.close();

        // return the count
        return totalCount;
    }

    //Get user gender
    public static String getProfileGenderInfo(Context context){

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query to get the gender of the user with the given username
        String query = "SELECT GENDER FROM users WHERE USER_NAME = ?";
        Cursor cursor = db.rawQuery(query, new String[]{CurrentUser.getInstance().getUserName()});
        String gender = null;
        if (cursor.moveToFirst()) {
            gender = cursor.getString(0);
        }
        cursor.close();
        return gender;
    }

    //Update profile data of the user
    public boolean updateProfileData(String userName ,String password, String gender){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USERS_COL_2, userName);
        contentValues.put(USERS_COL_3, password);
        contentValues.put(USERS_COL_4, gender);
        sqLiteDatabase.update(USERS_TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(CurrentUser.getInstance().getUserId())});
        sqLiteDatabase.close();
        return true;

    }

    public Integer deleteTodoList(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Integer deleteList = sqLiteDatabase.delete(TODO_LISTS_TABLE_NAME,"ID = ? AND USER_ID = ?", new String[] {id,String.valueOf(CurrentUser.getInstance().getUserId())});
        sqLiteDatabase.close();
        return deleteList;
    }

    public Integer deleteTodoItems(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Integer deleteTodo = sqLiteDatabase.delete(TODOS_TABLE_NAME,"LIST_ID = ? AND USER_ID = ?", new String[] {id,String.valueOf(CurrentUser.getInstance().getUserId())});
        sqLiteDatabase.close();
        return deleteTodo;
    }

    public boolean updateDashboardCheckBox(int id, int isChecked){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_COL_12, isChecked);
        sqLiteDatabase.update(TABLE_TASKS_NAME, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return true;

    }

}
