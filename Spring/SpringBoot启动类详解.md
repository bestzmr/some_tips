```java
public ConfigurableApplicationContext run(String... args) {
    // StopWatch：计时类，计算SpringBoot应用的启动时间
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    ConfigurableApplicationContext context = null;
  	// SpringBootExceptionReporter，是一个回调接口，用于支持SpringApplication启动错误的自定义报告。
    Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList();
    // configureHeadlessProperty，配置Headless模式配置，该模式下系统缺少显示设备、鼠标或键盘，而服务器端往往需要在该模式下工作
    this.configureHeadlessProperty();
  	// getRunListeners，获取SpringApplicationRunListeners类，是SpringApplicationRunListener类的集合
  	// SpringApplicationRunListener类：SpringApplication的run方法的监听器
    SpringApplicationRunListeners listeners = this.getRunListeners(args);
    listeners.starting();

    Collection exceptionReporters;
    try {
      	// DefaultApplicationArguments(args)类：提供访问运行一个SpringApplication的arguments的访问入口
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
      	// prepareEnvironment方法：创建和配置Environment，同时在调用AbstractEnvironment构造函数时生成PropertySourcesPropertyResolver类
        ConfigurableEnvironment environment = this.prepareEnvironment(listeners, applicationArguments);
      	// configureIgnoreBeanInfo(environment)方法：配置系统IgnoreBeanInfo属性
        this.configureIgnoreBeanInfo(environment);
        // 打印启动的图形，返回Banner接口实现类
        Banner printedBanner = this.printBanner(environment);
      	// createApplicationContext()方法：根据SpringApplication构造方法生成的webApplicationType变量创建一个ApplicationContext，默认生成AnnotationConfigApplicationContext
        context = this.createApplicationContext();
      	// getSpringFactoriesInstances(SpringBootExceptionRepoter.class, new Class[] {ConfigurableApplicationContext.class }, context)方法：获取SpringBootExceptionReporter类的集合
        exceptionReporters = this.getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, context);
      	// prepareContext(context, environment, listener, applicationArguments, printedBanner)方法：设置Environment，在ApplicationContext中应用所有相关的后处理，在刷新之前将所有的ApplicationContextInitializers应用于上下文，设置SpringApplicationRunLIstener接口实现类实现多路广播Spring事件，添加引导特定的单例（SpringApplicationArguments, Banner），创建DefaultListableBeanFactory工厂类，从主类中定位资源并将资源中的bean加载进入ApplicationContext中，向ApplicationContext中添加ApplicationListener接口实现类
        this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);
        // refreshContext(context)方法：调用AbstractApplicationContext的refresh()方法初始化DefaultListableBeanFactory工厂类
        this.refreshContext(context);
      	// afterRefresh(CongigurableApplicationContext context, ApplicationArguments args)方法：在刷新ApplicationContext之后调用，在SpringAppliation中是方法体为空的函数，故不做任何操作
        this.afterRefresh(context, applicationArguments);
        stopWatch.stop();
        if (this.logStartupInfo) {
            (new StartupInfoLogger(this.mainApplicationClass)).logStarted(this.getApplicationLog(), stopWatch);
        }
				// listeners.started(context)方法：在ApplicationContext已经刷新及启动后，但CommandLineRunners和ApplicationRunner还没有启动时，调用该方法向容器中发布SpringApplicationEvent类或者子类
        listeners.started(context);
      	// callRunners(context, applicationArguments)方法：调用应用中ApplicationRunner和CommanLineRunner的实现类，执行其run方法，调用时机是容器启动完成之后，可以用@Order注解来配置Runner的执行顺序，可以用来读取配置文件或连接数据库等操作
        this.callRunners(context, applicationArguments);
    } catch (Throwable var10) {
        this.handleRunFailure(context, var10, exceptionReporters, listeners);
        throw new IllegalStateException(var10);
    }

    try {
      	// listeners.running(context)方法：在容器刷新以及所有的Runner被调用之后，run方法完成执行之前调用该方法。调用之前得到的SpringApplicationRunListeners类running(context)方法
        listeners.running(context);
      	// 最后，向应用中返回一个之前获得到的ApplicationContext
        return context;
    } catch (Throwable var9) {
        this.handleRunFailure(context, var9, exceptionReporters, (SpringApplicationRunListeners)null);
        throw new IllegalStateException(var9);
    }
}
```