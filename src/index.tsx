import { NativeModules } from 'react-native';

type StackLoggerType = {
  multiply(a: number, b: number): Promise<number>;
};

const { StackLogger } = NativeModules;

export default StackLogger as StackLoggerType;
