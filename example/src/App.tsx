
import * as React from 'react';
import { StyleSheet, View, Text , TouchableOpacity } from 'react-native';
import StackLogger from 'react-native-stack-logger';

StackLogger.setTag('Exi');
StackLogger.setConsoleLogEnabled(true);
StackLogger.setFileLogEnabled(true);
StackLogger.setMaxFileSize(1024 * 1024);

export default class App extends React.Component {

componentWillMount(){
  StackLogger.requestAndroidStoragePermission();
};

componentDidCatch(error) {
  StackLogger.printError(error);
}

  render(){
  return (
    <View style={styles.container}>
      <TouchableOpacity onPress={()=>{

  StackLogger.printError("aa");
      }}><Text>Result: q</Text></TouchableOpacity>
      <TouchableOpacity onPress={()=>{
        StackLogger.printLog(new Error().stack);
      }}><Text>Stack: q</Text></TouchableOpacity>
    </View>
  );}
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
