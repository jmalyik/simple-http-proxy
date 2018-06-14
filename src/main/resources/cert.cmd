rem IMPORTANT: these commands must be used with Oracle JDK! With IBM JDK won't work!
rem Generate a Java keystore and a private key
keytool -genkeypair -alias serverkey -keyalg RSA -dname "CN=localhost, O=Magic, L=Budapest, ST=Hungary, C=HU" -keypass changeit -keystore server.jks -storepass changeit
keytool -genkeypair -alias clientkey -keyalg RSA -dname "CN=Client, O=Magic, L=Budapest, ST=Hungary, C=HU" -keypass changeit -keystore client.jks -storepass changeit
rem Export Client and server certificates
keytool -exportcert -alias clientkey -file client-public.cer -keystore client.jks -storepass changeit 
keytool -exportcert -alias serverkey -file server-public.cer -keystore server.jks -storepass changeit
rem Import client certificate onto server keystore (and vice versa)
keytool -importcert -keystore server.jks -alias clientcert -file client-public.cer -storepass changeit -noprompt 
keytool -importcert -keystore client.jks -alias servercert -file server-public.cer -storepass changeit -noprompt