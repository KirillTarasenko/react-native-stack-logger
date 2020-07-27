import { NativeModules, PermissionsAndroid } from 'react-native';

type StackLoggerType = {
  setTag(name: string): void;
  setConsoleLogEnabled(enabled: boolean): void;
  setFileLogEnabled(enabled: boolean): void;
  setMaxFileSize(sum: number): void;
  printLog(logStr: string): void;
  printError(logStr: string): void;
  requestAndroidStoragePermission(): void;
};


const requestAndroidStoragePermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
      {
        title: "Storage Permission",
        message:
          "Access for save logs (path: storage/StackLogger)",
        buttonNeutral: "Later",
        buttonNegative: "Cancel",
        buttonPositive: "OK"
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log("Logging ON");
    } else {
      console.log("Logging permission denied");
    }
  } catch (err) {
    console.warn(err);
  }
};

const printError = (error: any) =>{
  StackLogger.printError(`=====================\n   RN ERROR:   \n${error.message}\n=====================`
   + "\nJS Stack: "+ error.stack.substring(0, 150) +  "... \nRN Stack: "
   + ((new Error() || {}).stack || "").substring(0, 150) + "... \n==========================");

}

const { StackLogger } = NativeModules;
export default {...StackLogger, requestAndroidStoragePermission, printError} as StackLoggerType;
