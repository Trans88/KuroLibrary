# Kuro框架使用

框架的初衷是把一些我平时开发方便的功能集合起来，以便在不同项目中能够快速集成开发，后来由于集成的越来越多，一个sdk的体积过大，后拆分为KuroLibrary和KuroUI两个sdk,结果发现KuroLibrary的体积还是过大，之后如果有时间，还会细分，在此抱歉！



KuroLibrary集成了以下Api：

activity相关的：

|     Api名称     | 功能                                                         |
| :-------------: | :----------------------------------------------------------- |
| ActivityManager | 获取栈顶的Activity和对于前后台切换的通知管理                 |
|    AppGlobal    | 获取全局的Application,对于组件化项目，不可能把项目实际使用的Application下沉到Base,而且各个module也不需要Application真实名字,所以通过反射拿到全局Application |

file相关：

|   Api名称    | 功能                                                         |
| :----------: | :----------------------------------------------------------- |
| KuroFileUtil | 对文件进行操作：通过表单上传到对应url并回报进度，复制旧文件到新地址 |

log相关:

|    Api名称     | 功能                                                         |
| :------------: | :----------------------------------------------------------- |
| KuroLogManager | 管理KuroLog                                                  |
| KuroLogConfig  | 配置KuroLog,包括是否显示线程，设置全局的TAG,打印堆栈的深度，打印日志等级，设置打印器等等。 |
| KuroLogPrinter | KuroLog打印器，可以用户自己扩展，内置了控制台打印、app ui打印和本地日志打印 |

KuroUi集成了以下Api

| Api名称             | 功能                                                         |
| ------------------- | ------------------------------------------------------------ |
| KuroBanner          | 滚动图，支持是否自动播放，是否循环播放，是否显示指示器，切换指示器、自定义指示器等 |
| KuroRefreshLayout   | 下拉刷新组件，支持扩展KuroOverView实现自定义的刷新头         |
| KuroTabBottomLayout | 底部导航栏，支持单个tab设置高度、头部线条颜色、头部线条高度、底部透明度、支持文字、图片、字符显示图标。 |
| KuroTabTopLayout    | 顶部导航栏，支持自动滚动，实现点击的位置能够自动滚动展示前后2个 |



## 一、KuroLibrary



依赖方式

```
implementation 'com.github.Trans88:KuroLibrary:0.1.2'
```

### KuroLog

#### 1. 初始化

在程序入口调用init方法，该方法需要传2个参数，一个KuroLogConfig用来配置KuroLog,一个只定义的打印器，KuroLog中内置了三个打印器：KuroConsolePrinter在控制台打印日志，KuroFileLogPrinter将日志写入到文件，KuroViewPrinter 在程序中添加一个悬浮窗打印日志

```java
KuroLogManager.init(new KuroLogConfig() {
            @Override
            public int printLogLevel() {
            	//打印的日志等级
                return KuroLogType.V;
            }
            
			@Override
            public JsonParser injectJsonParser() {
            	//KuroLog支持对象的解析，这里自己配置对象序列化的方式，比如Gson
                return JsonParser {
                    Gson().toJson(it)
                }
            }
            
            @Override
            public String getGlobalTag() {
            	//打印的日志的默认TAG
                return "Taxiapp";
            }

            @Override
            public boolean enable() {
                return true;
            }

            @Override
            public boolean includeTread() {
            	//是否包含线程信息
                return true;
            }

            @Override
            public int stackTraceDepth() {
            	//打印日志的深度
                return 0;
            }
        },new KuroConsolePrinter(), KuroFileLogPrinter.getInstance(file.getPath(), 3*24*60*60*1000));
```

#### 2. 打印日志
```java
KuroLog.i("日志内容")
KuroLog.it("TAG","日志内容")
```
#### 3.如何实现自己的日志打印器
实现KuroLogPrinter接口，重写里面的print方法，以下是控制台打印器KuroConsolePrinter的实现：
```java
public class KuroConsolePrinter implements KuroLogPrinter {
    @Override
    public void print(@NonNull KuroLogConfig config, int level, String tag, @NonNull String printString) {
        int len =printString.length();
        int countOfSub =len/MAX_LEN;

        if (countOfSub>0){
            int index =0;
            for (int i =0;i<countOfSub;i++){
                Log.println(level,tag,printString.substring(index,index+MAX_LEN));
                index +=MAX_LEN;
            }
            //在没有整除的情况下把剩余的打印出来
            if (index !=len){
                Log.println(level,tag,printString.substring(index,len));
            }
        }else {
            //不足一行的时候将所有信息打印出来
            Log.println(level,tag,printString);
        }
    }
}
```

### KuroRestful
#### 简单使用方式：
内置Retrofit请求Factory,你也可以自己实现KuroCall的Factory接口实现基于其他框架的网络请求，比如Volley、okhttp、AsyncHttpClient。

1、创建接口，定义请求的方法，KuroCall里是请求的实例对象，例：
```kotlin
interface TaxiApi {
    @POST("xxxx",false)
    @CacheStrategy(CacheStrategy.CACHE_FIRST)
    fun registerApp(@Filed("xxx")xxx:String,@Header("xxxx")xxx:Boolean):KuroCall<RegisterResponse>
}
```
然后使用KuroApiFactory调用方法请求，例：
异步：
```kotlin
KuroApiFactory.create(TaxiApi::class.java)
            .registerApp(xxx,xxxx)
            .enqueue(object :KuroCallback<RegisterResponse>{
                override fun onSuccess(response: KuroResponse<RegisterResponse>) {
                    TODO("Not yet implemented")
                }

                override fun onFailed(throwable: Throwable) {
                    TODO("Not yet implemented")
                }

            })
```
同步：

```kotlin
val execute = KuroApiFactory.create(TaxiApi::class.java)
            .registerApp(xxx, xxxx)
            .execute()
```



未完！
