

emulator-5554primary�
�
SpotifyLoginActivityTestcom.example.harmonytestHandleAuthResponse_Success2������ȭ:�����̚�B
emulator-5554primary"�

logcatandroid�
�/Users/yankiozan/Desktop/Harmony2 2/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/logcat-com.example.harmony.SpotifyLoginActivityTest-testHandleAuthResponse_Success.txt"�

device-infoandroid�
�/Users/yankiozan/Desktop/Harmony2 2/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/device-info.pb"�

device-info.meminfoandroid�
/Users/yankiozan/Desktop/Harmony2 2/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/meminfo"�

device-info.cpuinfoandroid�
/Users/yankiozan/Desktop/Harmony2 2/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/cpuinfo*�
c
test-results.logOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriver�
�/Users/yankiozan/Desktop/Harmony2 2/app/build/outputs/androidTest-results/connected/debug/Medium_Phone_API_35(AVD) - 15/testlog/test-results.log 2
text/plain2�
QOcom.google.testing.platform.runtime.android.driver.AndroidInstrumentationDriver"INSTRUMENTATION_FAILED*OTest run failed to complete. Instrumentation run failed due to Process crashed.2�*�Logcat of last crash: 
Process: com.example.harmony, PID: 12849
java.lang.NullPointerException: Can't toast on a thread that has not called Looper.prepare()
	at com.android.internal.util.Preconditions.checkNotNull(Preconditions.java:174)
	at android.widget.Toast.getLooper(Toast.java:188)
	at android.widget.Toast.<init>(Toast.java:173)
	at android.widget.Toast.makeText(Toast.java:518)
	at android.widget.Toast.makeText(Toast.java:507)
	at com.example.harmony.MainActivity.lambda$getCurrentPlayingTrack$2$com-example-harmony-MainActivity(MainActivity.java:95)
	at com.example.harmony.MainActivity$$ExternalSyntheticLambda0.run(D8$$SyntheticClass:0)
	at java.lang.Thread.run(Thread.java:1012)
