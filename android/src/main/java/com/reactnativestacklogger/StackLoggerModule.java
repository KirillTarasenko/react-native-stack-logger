package com.reactnativestacklogger;

import android.util.Log;
import android.os.Environment;
import android.content.*;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import java.nio.file.*;
import javax.annotation.Nullable;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.app.ApplicationErrorReport.CrashInfo;
import android.os.Build;


public class StackLoggerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private String tag = "RNStackLogger";
  private boolean consoleLog = true;
  private boolean fileLog = false;
  private int maxFileSize = 512 * 1024; // 512 kb

  StackLoggerModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }

  @Override
  public String getName() {
    return "StackLogger";
  }
  
  @ReactMethod
  public void printLog(String content) {
      if (consoleLog) {
          Log.d(tag, content);
      }
      if (fileLog) {
          writeLogToFile(content);
      }
  }

  @Nullable
  File createLogFile(File folder, String name) {
      File f = new File(folder, name);
      try {
          if (f.createNewFile()) {
              return f;
          }
          return null;
      } catch (Exception e) {
          return null;
      }
  }


  void writeLogToFile(String content) {

      File logDirectory = new File( Environment.getExternalStorageDirectory() + "/StackLogger" );
      boolean success = false;
      if (!logDirectory.exists()) {
        success = logDirectory.mkdirs();
    }
    
      File logFolder = new File(logDirectory + "/" + tag);

      // create app folder
      if ( !logDirectory.exists() ) {
          logDirectory.mkdir();
      }

      // create log folder
      if ( !logFolder.exists() ) {
          logFolder.mkdir();
      }
      if (!logFolder.exists() && !logFolder.mkdir()) {
          return;
      }

      // get latest log file
      File[] logFiles = logFolder.listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File file, String name) {
              return name.endsWith(".txt");
          }
      });
      File logFile;
      if (logFiles == null || logFiles.length == 0) {
          logFile = createLogFile(logFolder, "0.txt");
      } else {
          // sort files by name
          Arrays.sort(logFiles, new Comparator<File>() {
              @Override
              public int compare(File a, File b) {
                  String fileName1 = a.getName().replaceAll("|\\.txt", "");
                  String fileName2 = a.getName().replaceAll("|\\.txt", "");
                  try {
                      int file1 = Integer.parseInt(fileName1);
                      int file2 = Integer.parseInt(fileName2);
                      return file1 - file2;
                  } catch (Exception e) {
                      Log.e("Error parse int", e.getMessage());
                  }
                  return 0;
              }
          });
          File lastLogFile = logFiles[logFiles.length - 1];
          if (lastLogFile.length() < maxFileSize) {
              logFile = lastLogFile;
          } else {
              int newNumber = Integer.parseInt(lastLogFile.getName().replaceAll("|\\.txt", "")) + 1;
              logFile = createLogFile(logFolder, newNumber + ".txt");
          }
      }
      if (logFile == null) {
          Log.e(tag, "Cannot create log file");
          return;
      }
      try {
          FileWriter fw = new FileWriter(logFile, true);
          String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
          fw.write(tag + " - " + currentTime + " - " + content);
          fw.append("\n\n");
          fw.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  @ReactMethod
  public void setTag(String tag) {
      this.tag = tag;
      writeLogToFile(startSession());
      writeLogToFile(reportDeviceInfo(tag));
      Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {

        String stackTraceString = Log.getStackTraceString(throwable);
        writeLogToFile(stackTraceString);
        }
    });
  }

  @ReactMethod
  public void setConsoleLogEnabled(boolean enabled) {
      this.consoleLog = enabled;
  }

  @ReactMethod
  public void setFileLogEnabled(boolean enabled) {
      this.fileLog = enabled;
  }

  @ReactMethod
  public void setMaxFileSize(int maxFileSize) {
      this.maxFileSize = maxFileSize;
  }

  @ReactMethod
  public void listAllLogFiles(Promise promise) {
      File logFolder = new File(this.reactContext.getFilesDir().getAbsolutePath() + "/rn-loggings");
      WritableArray result = new WritableNativeArray();
      if (!logFolder.exists() && !logFolder.mkdir()) {
          promise.resolve(result);
          return;
      }
      File[] logFiles = logFolder.listFiles(new FilenameFilter() {
          @Override
          public boolean accept(File file, String name) {
              return name.endsWith(".txt");
          }
      });
      for (int i = 0; i < logFiles.length; i++) {
          result.pushString(logFiles[i].getAbsolutePath());
      }
      promise.resolve(result);
  }



private static String reportDeviceInfo(String tag){

  return "\n************ DEVICE INFORMATION ***********\n"
          + "Brand: "
          + Build.BRAND
          + LINE_SEPARATOR
          + "Device: "
          + Build.DEVICE
          + LINE_SEPARATOR
          + "Model: "
          + Build.MODEL
          + LINE_SEPARATOR
          + "Id: "
          + Build.ID
          + LINE_SEPARATOR
          + "Product: "
          + Build.PRODUCT
          + "SDK: "
          + Build.VERSION.SDK_INT
          + LINE_SEPARATOR
          + "Tag Name: "
          + tag
          + LINE_SEPARATOR;
}




private static String startSession(){

  return "\n\n\n*******************************************\n"
       + "\n************ START NEW SESSION ************\n"
       + "\n*******************************************\n";
}


private static final String LINE_SEPARATOR = "\n";
  }