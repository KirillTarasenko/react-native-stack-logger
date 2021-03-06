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
  const errorStack = new Error().stack;
  StackLogger.printLog(`=====================\n   RN ERROR:   \n${error}\n=====================`
   +  "... \nJS Stack: " + (errorStack || "").replace(/\(http:\/\/\S+\)/g, '') + "... \n==========================");

}

const { StackLogger } = NativeModules;
export default {...StackLogger, requestAndroidStoragePermission, printError} as StackLoggerType;
