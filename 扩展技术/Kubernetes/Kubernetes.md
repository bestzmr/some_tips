1. 创建集群

### Kubernetes 集群

**Kubernetes 协调一个高可用计算机集群，每个计算机作为独立单元互相连接工作。** Kubernetes 中的抽象允许您将容器化的应用部署到集群，而无需将它们绑定到某个特定的独立计算机。为了使用这种新的部署模型，应用需要以将应用与单个主机分离的方式打包：它们需要被容器化。与过去的那种应用直接以包的方式深度与主机集成的部署模型相比，容器化应用更灵活、更可用。 **Kubernetes 以更高效的方式跨集群自动分发和调度应用容器。** Kubernetes 是一个开源平台，并且可应用于生产环境。

一个 Kubernetes 集群包含两种类型的资源:

- **Master** 调度整个集群
- **Nodes** 负责运行应用

![](..\..\imgs\kubernetes\集群图.svg)

**Master 负责管理整个集群。** Master 协调集群中的所有活动，例如调度应用、维护应用的所需状态、应用扩容以及推出新的更新。

**Node 是一个虚拟机或者物理机，它在 Kubernetes 集群中充当工作机器的角色** 每个Node都有 Kubelet , 它管理 Node 而且是 Node 与 Master 通信的代理。 Node 还应该具有用于处理容器操作的工具，例如 Docker 或 rkt 。处理生产级流量的 Kubernetes 集群至少应具有三个 Node 。

*Master 管理集群，Node 用于托管正在运行的应用。*

在 Kubernetes 上部署应用时，您告诉 Master 启动应用容器。 Master 就编排容器在集群的 Node 上运行。 **Node 使用 Master 暴露的 Kubernetes API 与 Master 通信。**终端用户也可以使用 Kubernetes API 与集群交互。

```shell
# 查看kubernetes的client 和 server版本，客户端版本是kubectl版本；服务器版本是安装在master上的 Kubernetes 版本
$ kubectl version
Client Version: version.Info{Major:"1", Minor:"20", GitVersion:"v1.20.4", GitCommit:"e87da0bd6e03ec3fea7933c4b5263d151aafd07c", GitTreeState:"clean", BuildDate:"2021-02-18T16:12:00Z", GoVersion:"go1.15.8", Compiler:"gc", Platform:"linux/amd64"}
Server Version: version.Info{Major:"1", Minor:"20", GitVersion:"v1.20.2", GitCommit:"faecb196815e248d3ecfb03c680a4507229c2a56", GitTreeState:"clean", BuildDate:"2021-01-13T13:20:00Z", GoVersion:"go1.15.5", Compiler:"gc", Platform:"linux/amd64"}
```

```shell
# 查看kubernetes集群的详细信息
$ kubectl cluster-info
Kubernetes control plane is running at https://172.17.0.37:8443
KubeDNS is running at https://172.17.0.37:8443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy
```

```shell
# 查看集群中的节点，该命令可以显示所有可以托管应用程序的节点，可以看到现在我们只有一个节点，可以看到它的状态是ready（准备接受部署的申请）
kubectl get nodes
$ kubectl get nodes
NAME       STATUS   ROLES                  AGE     VERSION
minikube   Ready    control-plane,master   4m20s   v1.20.2
```

2. 部署应用

### Kubernetes 部署

一旦运行了 Kubernetes 集群，就可以在其上部署容器化应用程序。 为此，您需要创建 Kubernetes **Deployment** 配置。Deployment 指挥 Kubernetes 如何创建和更新应用程序的实例。创建 Deployment 后，Kubernetes master 将应用程序实例调度到集群中的各个节点上。

创建应用程序实例后，Kubernetes Deployment 控制器会持续监视这些实例。 如果托管实例的节点关闭或被删除，则 Deployment 控制器会将该实例替换为群集中另一个节点上的实例。 **这提供了一种自我修复机制来解决机器故障维护问题。**

在没有 Kubernetes 这种编排系统之前，安装脚本通常用于启动应用程序，但它们不允许从机器故障中恢复。通过创建应用程序实例并使它们在节点之间运行， Kubernetes Deployments 提供了一种与众不同的应用程序管理方法。

![](..\..\imgs\kubernetes\部署应用.svg)



您可以使用 Kubernetes 命令行界面 **Kubectl** 创建和管理 Deployment。Kubectl 使用 Kubernetes API 与集群进行交互。

创建 Deployment 时，您需要指定应用程序的容器映像以及要运行的副本数。



```shell
# kubectl create deployment deployment的名称 镜像的位置
$ kubectl create deployment kubernetes-bootcamp --image=gcr.io/google-samples/kubernetes-bootcamp:v1
```

```shell
# 列出所有的deployment
$ kubcetl get deployments
```



3. 查看Pod和工作节点

## Kubernetes Pods

在创建 Deployment 时, Kubernetes 添加了一个 **Pod** 来托管你的应用实例。Pod 是 Kubernetes 抽象出来的，表示一组一个或多个应用程序容器（如 Docker），以及这些容器的一些共享资源。这些资源包括:

- 共享存储，当作卷
- 网络，作为唯一的集群 IP 地址
- 有关每个容器如何运行的信息，例如容器映像版本或要使用的特定端口。

Pod 为特定于应用程序的“逻辑主机”建模，并且可以包含相对紧耦合的不同应用容器。例如，Pod 可能既包含带有 Node.js 应用的容器，也包含另一个不同的容器，用于提供 Node.js 网络服务器要发布的数据。Pod 中的容器共享 IP 地址和端口，始终位于同一位置并且共同调度，并在同一工作节点上的共享上下文中运行。

Pod是 Kubernetes 平台上的原子单元。 当我们在 Kubernetes 上创建 Deployment 时，该 Deployment 会在其中创建包含容器的 Pod （而不是直接创建容器）。每个 Pod 都与调度它的工作节点绑定，并保持在那里直到终止（根据重启策略）或删除。 如果工作节点发生故障，则会在群集中的其他可用工作节点上调度相同的 Pod。

![](..\..\imgs\kubernetes\pod.svg)

## 工作节点

一个 pod 总是运行在 **工作节点**。工作节点是 Kubernetes 中的参与计算的机器，可以是虚拟机或物理计算机，具体取决于集群。每个工作节点由主节点管理。工作节点可以有多个 pod ，Kubernetes 主节点会自动处理在群集中的工作节点上调度 pod 。 主节点的自动调度考量了每个工作节点上的可用资源。

每个 Kubernetes 工作节点至少运行:

- Kubelet，负责 Kubernetes 主节点和工作节点之间通信的过程; 它管理 Pod 和机器上运行的容器。
- 容器运行时（如 Docker）负责从仓库中提取容器镜像，解压缩容器以及运行应用程序。

![](..\..\imgs\kubernetes\工作节点.svg)

## 使用 kubectl 进行故障排除

使用它来获取有关已部署的应用程序及其环境的信息。 最常见的操作可以使用以下 kubectl 命令完成：

- **kubectl get** - 列出资源
- **kubectl describe** - 显示有关资源的详细信息
- **kubectl logs** - 打印 pod 和其中容器的日志
- **kubectl exec** - 在 pod 中的容器上执行命令

您可以使用这些命令查看应用程序的部署时间，当前状态，运行位置以及配置。



```shell
# 查看已存在的pod
$ kubectl get pods
```

```shell
# 查看pod中的容器以及构建容器的镜像（还包括有关 Pod 容器的详细信息：IP 地址、使用的端口以及与 Pod 生命周期相关的事件列表等）
$ kubectl describe pods
```





4. 使用Service暴露应用

### Kubernetes Service 总览

Kubernetes [Pod](https://kubernetes.io/zh/docs/concepts/workloads/pods/) 是转瞬即逝的。 Pod 实际上拥有 [生命周期](https://kubernetes.io/zh/docs/concepts/workloads/pods/pod-lifecycle/)。 当一个工作 Node 挂掉后, 在 Node 上运行的 Pod 也会消亡。 [ReplicaSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/replicaset/) 会自动地通过创建新的 Pod 驱动集群回到目标状态，以保证应用程序正常运行。 换一个例子，考虑一个具有3个副本数的用作图像处理的后端程序。这些副本是可替换的; 前端系统不应该关心后端副本，即使 Pod 丢失或重新创建。也就是说，Kubernetes 集群中的每个 Pod (即使是在同一个 Node 上的 Pod )都有一个惟一的 IP 地址，因此需要一种方法自动协调 Pod 之间的变更，以便应用程序保持运行。

Kubernetes 中的服务(Service)是一种抽象概念，它定义了 Pod 的逻辑集和访问 Pod 的协议。Service 使从属 Pod 之间的松耦合成为可能。 和其他 Kubernetes 对象一样, Service 用 YAML [(更推荐)](https://kubernetes.io/zh/docs/concepts/configuration/overview/#general-configuration-tips) 或者 JSON 来定义. Service 下的一组 Pod 通常由 *LabelSelector* (请参阅下面的说明为什么您可能想要一个 spec 中不包含`selector`的服务)来标记。

尽管每个 Pod 都有一个唯一的 IP 地址，但是如果没有 Service ，这些 IP 不会暴露在群集外部。Service 允许您的应用程序接收流量。Service 也可以用在 ServiceSpec 标记`type`的方式暴露

- *ClusterIP* (默认) - 在集群的内部 IP 上公开 Service 。这种类型使得 Service 只能从集群内访问。
- *NodePort* - 使用 NAT 在集群中每个选定 Node 的相同端口上公开 Service 。使用`<NodeIP>:<NodePort>` 从集群外部访问 Service。是 ClusterIP 的超集。
- *LoadBalancer* - 在当前云中创建一个外部负载均衡器(如果支持的话)，并为 Service 分配一个固定的外部IP。是 NodePort 的超集。
- *ExternalName* - 通过返回带有该名称的 CNAME 记录，使用任意名称(由 spec 中的`externalName`指定)公开 Service。不使用代理。这种类型需要`kube-dns`的v1.7或更高版本。

另外，需要注意的是有一些 Service 的用例没有在 spec 中定义`selector`。 一个没有`selector`创建的 Service 也不会创建相应的端点对象。这允许用户手动将服务映射到特定的端点。没有 selector 的另一种可能是您严格使用`type: ExternalName`来标记。



![](..\..\imgs\kubernetes\service.svg)

Service 通过一组 Pod 路由通信。Service 是一种抽象，它允许 Pod 死亡并在 Kubernetes 中复制，而不会影响应用程序。在依赖的 Pod (如应用程序中的前端和后端组件)之间进行发现和路由是由Kubernetes Service 处理的。

Service 匹配一组 Pod 是使用 [标签(Label)和选择器(Selector)](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/labels), 它们是允许对 Kubernetes 中的对象进行逻辑操作的一种分组原语。标签(Label)是附加在对象上的键/值对，可以以多种方式使用:

- 指定用于开发，测试和生产的对象
- 嵌入版本标签
- 使用 Label 将对象进行分类



![](..\..\imgs\kubernetes\label.svg)

标签(Label)可以在创建时或之后附加到对象上。他们可以随时被修改。现在使用 Service 发布我们的应用程序并添加一些 Label 。



```shell
# 列出当前集群的所有服务
$ kubectl get services
# 暴露服务
$ kubectl expose deployment/kubernetes-bootcamp --type="NodePort" --port 8080
# 查看服务的详细信息
$ kubectl describe services/kubernetes-bootcamp
# 可查看label标签和selector选择器
$ kubectl describe deployment
# 根据标签查询pod列表
$ kubectl get pods -l app=kubernetes-bootcamp
# 根据标签列出service
$ kubectl get services -l app=kubernetes-bootcamp
# 根据标签删除服务
$ kubectl delete service -l app=kubernetes-bootcamp
```





5. 扩容和缩放

### 扩缩应用程序

在之前的模块中，我们创建了一个 [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/)，然后通过 [Service](https://kubernetes.io/zh/docs/concepts/services-networking/service/)让其可以开放访问。Deployment 仅为跑这个应用程序创建了一个 Pod。 当流量增加时，我们需要扩容应用程序满足用户需求。

**扩缩** 是通过改变 Deployment 中的副本数量来实现的。


![](..\..\imgs\kubernetes\扩缩1.svg)

扩容前

![](..\..\imgs\kubernetes\扩缩2.svg)扩容后

扩展 Deployment 将创建新的 Pods，并将资源调度请求分配到有可用资源的节点上，收缩 会将 Pods 数量减少至所需的状态。Kubernetes 还支持 Pods 的[自动缩放](https://kubernetes.io/zh/docs/tasks/run-application/horizontal-pod-autoscale/)，但这并不在本教程的讨论范围内。将 Pods 数量收缩到0也是可以的，但这会终止 Deployment 上所有已经部署的 Pods。

运行应用程序的多个实例需要在它们之间分配流量。服务 (Service)有一种负载均衡器类型，可以将网络流量均衡分配到外部可访问的 Pods 上。服务将会一直通过端点来监视 Pods 的运行，保证流量只分配到可用的 Pods 上。

*扩缩是通过改变 Deployment 中的副本数量来实现的。*



一旦有了多个应用实例，就可以没有宕机地滚动更新。

```shell
# 查看副本
$ kubectl get rs
$ kubectl get rs
NAME                            DESIRED   CURRENT   READY   AGE
kubernetes-bootcamp-fb5c67579   1         1         1       34s

DESIRED 显示您在创建部署时定义的应用程序副本的所需数量。这是理想的状态。

CURRENT 显示当前正在运行的副本数量

我们将使用 kubectl scale 命令，然后是部署类型、名称和所需的实例数量来缩放部署的副本数量
$ kubectl scale deployments/kubernetes-bootcamp --replicas=4
$ kubectl get deployments
# 查看pods
$ kubectl get pods -o wide

```

