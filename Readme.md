### 说明

**一、工程说明：**

- ``app``：主工程module
- ``businessmodule1``：组件化业务模块1
- ``businessmodule2``：组件化业务模块2
- ``easyRouter-core``：路由框架主module
- ``easyRouter-annotation``：路由框架注解module
- ``easyRouter-compiler``：注解处理器

**二、组件化说明：**

- 工程根目录下 新建``config.gradle``，其中``isModule``为true时，两个业务module会集成到主app中，为false时，业务module可独立运行。

  ```java
  ext {
      //true 集成模式 false 组件模式
      isModule = true
  }
  ```

  在根目录的``build.gradle``中引入

  ```java
  apply from: "config.gradle"
  ```

- 在业务module下``build.gradle``中，配置:

  ```java
  if (isModule) {
      apply plugin: 'com.android.library'
  } else {
      apply plugin: 'com.android.application'
  }
  ......
  android{
      ....
      resourcePrefix "${project.name}_" //给 Module 内的资源名增加前缀, 避免资源名冲突。此处可自行设置。
      defaultConfig{
         if (!isModule) {
              applicationId "xxxxxx"
          } 
          ......
          sourceSets {
              main {
                  if (isModule) {
                      manifest.srcFile 'src/main/AndroidManifest.xml'
                  } else {
                      manifest.srcFile 'src/main/module/AndroidManifest.xml'
                          java.srcDirs 'src/main/module/java', 'src/main/java'
                  }
              }
          }
      }    
  }
  ```

  其中，main目录下的``AndroidManifest.xml``编写为：

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="xxxxxxx">
      <application>
          <activity android:name=".MainActivity"/>
      </application>
  </manifest>
  ```

  另外，还要在main目录下，新建文件夹``module``，新建``AndroidManifest.xml``，编写为：

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <manifest xmlns:android="http://schemas.android.com/apk/res/android"
      package="xxxxxxx">
      <application
          android:allowBackup="true"
          android:icon="@mipmap/businessmodule1_ic_launcher"
          android:label="@string/app_name"
          android:roundIcon="@mipmap/businessmodule1_ic_launcher_round"
          android:supportsRtl="true"
          android:theme="@style/businessmodule1_Theme.EasyRouter">
          <activity android:name=".MainActivity">
              <intent-filter>
                  <action android:name="android.intent.action.MAIN" />
                  <category android:name="android.intent.category.LAUNCHER" />
              </intent-filter>
          </activity>
      </application>
  </manifest>
  ```

- 在``app``主module中的``build.gradle``添加：

  ```java
  dependencies {
      if (isModule) {
          implementation project(':businessmodule1')
          implementation project(':businessmodule2')
      }
  }
  ```

**三、路由框架使用**

- 在需要使用路由框架的主module或者业务module中，``build.gradle``下添加：

  ```java
  ......
  android {
      ......
      defaultConfig {
          ......
          javaCompileOptions {
              annotationProcessorOptions {
                  arguments = [moduleName: project.name]//project.name可以替换为自己的名称，但是必须全工程唯一，建议直接使用此处设置方式。
              }
          }
          ......
      }
      ......
  }
  ......
  dependencies {
      implementation project(":easyRouter-core")
      annotationProcessor project(":easyRouter-compiler")
  }
  ```

- 在主工程的application中，进行初始化：

  ```java
  public class MyApplication extends Application {
      @Override
      public void onCreate() {
          super.onCreate();
          EasyRouter.init(this);//初始化
          ......
      }
  }
  ```

- 在需要用到路由跳转的Activity页面中，添加注解``EasyRoute``。其中，path的值必须唯一，path即后续要用到的跳转链接。

  ```java
  @EasyRoute(path = "/module2/main")
  public class MainActivity extends AppCompatActivity {
      ......
  }
  ```

- 跳转使用：

  - 不带参数的直接跳转，build中的参数，即需要跳转到的目的页面的注解中的path值。

  ```java
  EasyRouter.getInstance().build("/module2/main").navigation();
  ```

  - 带参数的跳转，多个参数时，可以在navigation之前，继续``.withXXX``。

  ```java
  EasyRouter.getInstance().build("/module2/main").withString("value","123456").navigation();
  ```

  - 带返回值的跳转：

  ```java
  EasyRouter.getInstance()
      .build("/module2/main")
      .withString("value","123456")
      .navigationForResult(MainActivity.this,123);
  ```

- 对`Fragment`的支持：

  ```java
  Fragment fragment = (Fragment) EasyRouter.getInstance()
      .build("/module1/business1Fragment")
      .withString("value","123456")
      .navigation();
  ```

  通过上述方式获取到fragment对象，获取到的`fragment`是`android.app.Fragment`还是`android.support.v4.app.Fragment`，需要看对方创建的Fragment是什么类型。

- 调用`service`

  启动：

  ```java
  EasyRouter.getInstance().build("/module1/myService").navigation();
  ```

  停止：

  ```java
  EasyRouter.getInstance().build("/module1/myService").stopNavigation();
  ```