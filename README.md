﻿﻿﻿# 트래픽 모니터링 어플리케이션



## 목차

[1. 프로젝트 개요](#1. 프로젝트 개요)

​	[1.1. 목적](#1.1. 목적)

​	[1.2. 요구사항](#1.2. 요구사항)

​	[1.3. 개발 환경](#1.3. 개발환경)

[2. 기능 설명](#2. 기능 설명)

​	[2.1. 프로그램 흐름도](#2.1. 프로그램 흐름도)

​	[2.2. 인터페이스 및 사용법](#2.2. 인터페이스 및 사용법)

[3. 클래스 및 소스코드](#3. 클래스 및 소스코드)

​	[3.1. 클래스 다이어그램](#3.1. 클래스 다이어그램)

​	[3.2. 클래스 설명](#3.2. 클래스 설명)

​	[3.3. 소스코드](#3.3. 소스코드)

[4. 평가 및 개선 방향](#4. 평가 및 개선 방향)

​	[4.1. 평가 및 검증](#4.1. 평가 및 검증)

​	[4.2. 개선 방향](#4.2. 개선 방향)

<br>

## 1. 프로젝트 개요

#### 1.1. 목적

 스마트폰의 송신 트래픽을 감지하고 트래픽 내역(이후 트래픽 내역 용어를 트래픽 로그, 트래픽 히스토리와 혼용하여 사용한다)을 출력하는 어플리케이션을 개발한다. 

 스마트폰 점유율의 증가에 따라 악성 어플리케이션을 이용한 공격의 위험성이 대두되고 있다. 스마트폰의 이상 행동 탐지 기법 중에서 사용자가 단말기에 발생시키는 이벤트와 앱의 네트워크 트래픽 발생 시간 및 횟수 조사를 통해 탐지하는 기법이 존재한다.[^1] 따라서 어플리케이션들의 송신 트래픽 발생 내역을 사용자에게 알려줌으로써 악성 어플리케이션에 의한 공격 방지에 도움을 줄 수 있다. 본 프로젝트에서는 스마트폰의 이상 행동 탐지를 위해 송신 트래픽을 감지하고 트래픽 내역을 출력하는 애플리케이션을 개발한다.

<br>

#### 1.2. 요구사항

- ‘TrafficMonitor’ 클래스와 ‘MainActivity’ 클래스를 분리한다.
- UID별 트래픽 전송량의 누적 바이트 수를 반환하고 GUI로 제공한다.
- 멀티 쓰레드로 시간 간격별로 구분 가능하게 한다.
- 트래픽 내역을 파일로 저장한다.

<br>

#### 1.3. 개발환경

- PC OS: Windows10 64bit
- JAVA 버전: 14.0.2
- IDE: Android Studio Arctic Fox (2020.3.1), IntelliJ IDEA 2020.3.4 (Ultimate Edition)
- Android SDK: Android 10.0+ (R)
- 모바일 Android 버전: 10

<br>

## 2. 기능 설명

#### 2.1. 프로그램 흐름도

![Flow Chart](https://user-images.githubusercontent.com/55964775/182070896-b68b00f1-0cee-41a9-baf9-8570880a4eeb.png)


<br>

#### 2.2. 인터페이스 및 사용법

- 화면 구성

![2](https://user-images.githubusercontent.com/55964775/129677428-9be6efeb-c788-40aa-861d-3188eb52109c.png)

 

- 스위치 작동(스토리지 접근 권한이 없는 경우)

![3](https://user-images.githubusercontent.com/55964775/129677429-1b989205-eae1-4e25-8ba8-c8bf0a4b7d00.png)

스위치를 켰지만 스토리지 접근 권한이 없는 경우 해당 권한 설정 탭을 연다.

- 스위치 작동(사용 정보 권한이 없는 경우)

![4](https://user-images.githubusercontent.com/55964775/129677430-7ec3c225-1813-4e96-babb-b6d8945d37cc.png)

스위치를 켰지만 앱의 사용 정보 허용 권한이 없는 경우 알림 메시지와 함께 해당 설정 페이지로 이동시킨다.

- 스위치 작동(트래픽 모니터링 시작)

![5](https://user-images.githubusercontent.com/55964775/129677431-59ecdc2c-b07e-4bdb-b742-b6713d7f7655.png)

모니터링이 시작되면 20초 간격으로 송신 트래픽을 감지하여 리스트 뷰에 보여준다.



- 작동 중단

![6](https://user-images.githubusercontent.com/55964775/129677434-e358672b-b3ff-49d5-bf19-2174e3fbad31.png)

스위치를 다시 끄면 모니터링을 중단한다.

- 로그 파일 확인

![7](https://user-images.githubusercontent.com/55964775/129677436-1df5fdfd-9138-45e3-9cd8-cd63a13c5381.png)

 파일 탐색기 어플을 이용하거나 PC와의 연결을 통해 디바이스 내부 저장소의 ‘TrafficMonitor’ 디렉토리에서 트래픽 내역 로그 파일을 열 수 있다.

<br>

## 3. 클래스 및 소스코드

#### 3.1. 클래스 다이어그램

![Class Diagram](https://user-images.githubusercontent.com/55964775/182071204-29689568-0440-41ed-8e80-438b3addc087.png)
<br>

#### 3.2. 클래스 설명

- MainActivity Class

 앱을 실행시켰을 때 처음 수행되는 메인 클래스이다. 모니터링 작동 스위치(switchTracking), 작동 여부를 알려주는 버튼(buttonStatus), 트래픽 내역 리스트 뷰(listViewHistory), 메인 액티비티(activity) 변수를 포함하며, onCreate() 메소드를 통해 메인 UI 쓰레드를 작동시킨다. 리스트 뷰는 AdapterHistory 클래스를 통해서 컨텐츠를 받아오고, 스위치가 켜졌는지 여부에 따라 TrafficMonitor 클래스의 기능들을 수행시킨다.

- AdapterHistory Class

 메인 클래스의 리스트 뷰와 Histories 클래스의 자료구조를 연결시켜주는 어댑터 클래스이다. ‘BaseAdapter’ 추상클래스를 상속받아 4개의 메서드들을 구현하였다. ‘getCout()’, ‘getItem()’, 메소드들은 Histories 인스턴스 메소들을 이용하여 값을 리턴한다. ‘getView()’ 메소드는 패키지의 res/layout 디렉토리에서 ‘listview\_custom.xml’을 가져와서 값들을 세팅하고 리턴한다.

- Histories Class

 트래픽 히스토리 내역들을 저장하는 리스트인 ‘histories’ 자료구조와 이를 관리하는 메서드들로 구성된 클래스이다. ‘histories’ 리스트는 History 인스턴스들을 저장한다.

- History Class

 발생한 트래픽 정보를 저장하는 클래스이다. private 변수로 ‘time’, ‘appLabel’, ‘appProcessName’, ‘uid’, ‘usage’, ‘diff’를 가지고 있으며 이 변수들에 대한 getter 메소드들로 이루어져 있다. time 변수는 트래픽이 감지된 시간, appLabel 변수는 앱의 이름, appProcessName 변수는 앱 프로세스 이름, uid 변수는 앱의 UID, usage 변수는 누적 송신 트래픽의 양, diff 변수는 트래픽 증가량을 저장한다. 

- TrafficMonitor Class

 송신 트래픽 모니터링 기능을 담당하는 클래스이다. 

 private 변수로 lastUsage와 appNames Map 자료구조를 가진다. lastUsage Map은 어플 별로 최근에 업데이트된 트래픽 양을 저장하고, appNames Map은 앱의 process name과 일반 이름을 매핑시킨다. 메소드들은 ‘checkPermissions()’, ‘initializeTraffic()’, ‘startTracking()’, ‘updateUsage()’가 있다.

  ‘checkPermissions()’는 이 어플에 스토리지 접근 권한과 사용 정보 권한이 존재하는지를 체크하는 메소드이다. 스토리지 접근 권한은 LogExternalFileProcessor 클래스의 메소드를 사용하여 체크하고, 사용 정보 권한은 try-catch 문을 이용하여 다른 어플의 네트워크 정보를 가져오는데 오류가 발생하는지 확인하여 체크한다. 만약 권한이 존재하지 않는다면 유저를 해당 권한 설정 탭 및 페이지로 이동시키고, false를 리턴한다. 그리고 권한이 존재한다면 true를 리턴한다.

 ‘initializeTraffic()’ 메소드는 클래스의 자료구조들을 초기화한다. 디바이스에 설치된 앱들의 정보를 가져와서 appNames Map에 저장하고, 현재까지 어플들의 트래픽 내역을 lastUsage Map에 업데이트 한다.

 ‘startTracking()’ 메소드는 20초 간격으로 ‘updateUsage()’ 메소드를 작동시킨다. ‘startTracking()’ 메소드에는 2개의 타이머가 존재한다. 하나는 20초 간격으로 ‘updateUsage()’를 작동시키는 타이머이고, 다른 하나는 10초 간격으로 메인 화면의 스위치가 켜져 있는지 조사하여 타이머들의 작동 여부를 결정한다. 만약 메인 화면의 스위치가 꺼진다면 후자의 타이머가 이를 감지하여 전자의 타이머와 자기 자신을 close시킨다.

 ‘updateUsage()’ 메소드는 발생한 트래픽을 감지하여 ‘lastUsage’에 업데이트한다. 트래픽을 감지하면 콘솔창에 로그를 출력하고, ‘LogExternalFileProcessor’ 클래스를 이용하여 로그 파일로 작성한다. 어플 별로 사용한 트래픽 양을 구하기 위해‘NetworkStatsManager.querySummary()’ 라이브러리를 이용한다.

- LogExternalFileProcessor Class

 LogExternalFileProcessor Class는 패키지 외부 스토리지인의 로그 파일을 관리하는 클래스이다. LogFileProcessor Interface를 implements하여 구현하였다. 메소드는 두 가지가 있다.  ‘checkStoragePermission()’ 메소드는 앱이 스토리지 접근 권한이 있는지 체크한다. 앱 패키지 외부 저장 공간에 접근하기 위해서는 권한이 필요하다. 만약 이 권한이 존재하지 않는다면 해당 설정 탭을 열어주고 false를 리턴한다. 권한이 존재한다면 true를 리턴한다.

‘writeLog()’ 메소드는 csv 확장자를 가지는 로그 파일을 열어서 인자로 전달받은 로그를 작성한다. 로그 파일은 디바이스 내부 저장소에 ‘TrafficMonitor’ 폴더를 생성하여, 해당 폴더 내부에 저장한다. 

- LogFileProcessor Interface

 LogFileProcessor Interface는 로그 파일 관리를 담당하는 클래스를 구현하기 위한 인터페이스이다. LogExternalFileProcessor 클래스와 통합하지 않고 인터페이스로 따로 구현한 이유는 기능의 확정성을 위해서이다. 

![Interface](https://user-images.githubusercontent.com/55964775/182071238-43e74163-4ca8-46b3-aa8f-c03b848ddeed.png)

 위의 다이어그램은 LogfileProcessor 인터페이스와 이를 통해 구현한 클래스들을 나타낸다. LogExternalFileProcessor 클래스는 패키지 외부에 있는 디바이스 스토리지인 ‘/sdcard’ 디렉토리에 로그 파일을 관리하는 클래스이고, LogInternalFileProcessor 클래스는 앱 패키지 내부 스토리지인 ‘/data/data/com.example.trafficmonitorapp/files’ 디렉토리에 로그 파일을 관리하는 클래스이다. 개발 초기에는 LogInternalFileProcessor 클래스를 먼저 구현하였지만 로그 파일을 앱 패키지 내부에 저장하여 관리하는 것 보다 패키지 외부에 저장하여 관리하는 것이 실용적이기 때문에 기능을 추가하였다. 이때 기존 클래스를 수정하는 대신, interface를 만들고 이를 구현하는 클래스를 따로 만들어서 클래스의 단일책임원칙을 지키고 확장성을 높였다. 현재 어플리케이션에서 LogInternalfileProcessor 클래스는 사용되지 않고 있지만 필요에 따라 해당 클래스로 사용할 수 있고, LogFileProcessor 인터페이스를 통해 다른 기능을 가진 클래스들을 만들어 확장할 수 있다.

<br>

#### 3.3. 소스코드

전체 소스코드는 아래 깃허브 링크에서 확인 가능하다.

<https://github.com/boorooksus/Traffic_Monitor_App.git>

<br>

## 4. 평가 및 개선 방향

#### 4.1. 평가 및 검증

 본 프로젝트에서 구현한 어플리케이션은 요구사항들을 모두 만족하고 에러 없이 잘 동작한다. TrafficMonitor 클래스를 메인 클래스와 분리하고, 멀티 쓰레딩을 통해 백그라운드에서 트래픽을 모니터링하였으며, 어플 별 발생한 송신 트래픽을 화면의 리스트 뷰에 출력하고 파일에 기록하였다. 

 백그라운드에서 모니터링을 제대로 수행하는지 검증하기 위해 모니터링을 작동시킨 뒤 어플을 종료해보았다. 만약 모니터링이 백그라운드에서 실행된다면 어플이 종료되어 메인 쓰레드가 종료된 뒤에도 로그 파일에 감지된 트래픽 내역을 남길 것이다. 그리고 다음 날 로그 파일을 확인한 결과, 아래와 이미지와 같이 앱의 종료 이후 6시간 30분가량 모니터링이 진행된 것을 확인할 수 있었다.

![10](https://user-images.githubusercontent.com/55964775/129677443-db48cc17-01a8-4813-846b-ce40825bacfe.png)

 그리고 트래픽 내역 로그파일을 확인한 결과, 아래 그림과 같이 포어그라운드에서 작동하는 애플리케이션의 송신 트래픽(카카오톡, 배달의 민족 어플 등) 뿐만 아니라 백그라운드에서 발생한 트래픽(Facebook Services, Google Play 스토어 등)과 이름 없는 시스템 프로세스(app label이 ‘Untitled’인 프로세스)의 트래픽도 탐지하였다.

![11](https://user-images.githubusercontent.com/55964775/129677444-7247f2b3-d810-48b0-8750-bb894ac8e4a2.png)

 마지막으로 감지된 트래픽 증가량이 정확한지 검증하기 위해 테스트 앱과 서버를 만들어 확인해보았다. 테스트 앱은 숫자를 입력 받아 버튼을 누르면 TCP 통신을 위한 소켓을 생성하고, 입력된 숫자를 integer type으로 소켓에 담아 테스트 서버에 전송한다. 그리고 테스트 서버는 어플로부터 전송된 데이터를 받는 프로그램이다. 전체 소스코드는 아래 깃허브 링크에서 확인 가능하다. 

<https://github.com/boorooksus/Network_Test.git>

 테스트 어플을 트래픽 모니터링 어플이 설치된 디바이스에 설치하고 테스트를 진행하였다. 테스트 서버 프로그램과 테스트 어플을 작동시킨 뒤, 왼쪽 그림과 같이 테스트 어플에서 숫자 ‘1’을 입력하고 버튼을 눌렀다. 그 결과 트래픽 모니터링 앱은 테스트 앱의 트래픽을 감지하는데 성공하였다. 그리고 아래 이미지와 같이 트래픽 내역을 화면에 출력하고 로그 파일에 작성하였다.

![12](https://user-images.githubusercontent.com/55964775/129677446-1aeb2109-b0d7-4e00-8a8f-075238addf0a.png)

 위의 이미지에서 로그 파일 ‘usage’ 항목은 누적 트래픽 사용량을, ‘increase’ 항목은 트래픽 증가량을 나타낸다. 테스트 앱에서 264 바이트의 전송 트래픽을 감지하였다. 이 수치가 일정한지 알아보기 위해 동일한 조건에서 한 번 더 테스트 앱을 작동시켰다. 트래픽 모니터링 앱은 이전과 같이 264 바이트의 전송 트래픽을 감지하여 로그 파일에 아래의 내용을 작성하였다.

![13](https://user-images.githubusercontent.com/55964775/129677447-fa7361e9-f215-4baf-b3be-afda6295106d.png)

 첫 번째와 두 번째 테스트 모두 트래픽이264 바이트 씩 증가하여 누적 사용량이 264 바이트, 528 바이트가 된 것이 확인되었다. JAVA 프로그래밍 언어에서 Integer 타입은 4바이트이다. 하지만 트래픽 모니터링 앱에서 증가된 트래픽은 4바이트가 아닌 264바이트이다. 이렇게 전송한 데이터 크기와 차이 나는 이유는 서버와 통신하기 위해 추가적인 트래픽을 사용하기 때문이다. 테스트 앱은 서버와 TCP로 통신한다. TCP에서 3-Way Handshake 방식을 사용하기 때문에 서버와 클라이언트가 서로 연결되었는지 체크하는 트래픽을 추가로 사용한다. 이를 확인하기 위해 테스트 앱에서 전송 버튼을 세 번째로 누를 때에는 서버 프로그램을 중단한 뒤에 눌렀다. 그 결과 아래 로그 파일 이미지와 같이 120 바이트의 트래픽만 감지되었다. 

![14](https://user-images.githubusercontent.com/55964775/129677448-38c583e9-231d-4b5f-99e6-b934d8087488.png)

 뿐만 아니라 데이터를 담은 소켓이 목적지에 도착하기 위해 필요한 추가적인 데이터도 필요하기 때문에 전송한 데이터 크기와 차이가 난다. IP 주소, 포트 번호, Checksum bit 등의 네트워크 통신을 위한 데이터가 필요하다. 네 번째 테스트에서는 테스트 서버 프로그램과 앱의 소스 코드를 수정하고 작동시켰다. 수정된 소스 코드는 아래 이미지와 같이 입력된 정수 데이터를 소켓에 두 번 담아 주고받도록 변경하였다.

![15](https://user-images.githubusercontent.com/55964775/129677451-40432c48-dbd1-4cb9-a6ea-01b024dc073a.png)

테스트 결과 아래 이미지와 같이 트래픽 모니터링 앱이 268 바이트의 트래픽을 감지하였다. 첫 번째와 두 번째 전송할 때보다 소켓에 4바이트 정수가 추가로 담겼기 때문에 감지된 트래픽이 정확히 4바이트 증가되었다. 

![16](https://user-images.githubusercontent.com/55964775/129677455-f57039a6-62ee-40e8-bad8-b00b654e7812.png)

 이와 같은 테스트 결과들을 통해 트래픽 모니터링 앱에서 정확한 트래픽 량을 감지한다는 것을 검증하였다.

<br>

#### 4.2. 개선 방향

 먼저 트래픽 모니터링 기능 측면에서, 모바일 데이터의 트래픽 모니터링 기능을 추가하고 트래픽 감지 속도를 향상시켜야 한다. 현재 이 어플리케이션은 와이파이를 통한 트래픽만 조사할 수 있다. 따라서 와이파이를 이용할 수 없는 환경인 경우에서도 모니터링을 할 수 있도록 모바일 데이터 트래픽을 감지할 수 있는 방안을 모색해야한다. 그리고 현재는 트래픽이 발생한 뒤 리스트 뷰에 업데이트 되기까지 1~20초가량 걸린다. 악성 어플의 트래픽을 즉각적으로 감지하고 대처하기 위해서 트래픽 감지에 걸리는 시간을 단축시켜야 한다. 이를 위해서 트래픽을 감지하는 메소드를 실행시키는 타이머의 반복 주기를 3초로 단축시켜보았지만, 오히려 발생하는 트래픽을 감지하지 못하는 결과를 야기했다. 이는 백그라운드 쓰레드들의 개수가 증가하고, 같은 공유 변수나 자료구조에 접근하면서 동기화 문제가 발생하기 때문인 것으로 예상된다.  

 다음으로 UI 측면에서, 화면에 보이는 트래픽 내역 개수를 제한하고 사용자의 편의를 위한 기능을 추가해야 한다. 이 어플의 리스트 뷰는 조사된 모든 트래픽 내역을 출력하도록 설계되었다. 따라서 트래픽 내역이 많아질수록 출력해야 하는 데이터가 많아져 어플리케이션의 성능을 저하시킨다. 따라서 리스트 뷰에 30개 정도의 트래픽 내역만 보이도록 제한하고, 오래된 트래픽 내역은 페이지네이션으로 찾아볼 수 있게 해야 한다. 또한 트래픽 내역을 기간별, 어플 별로 조사하거나 사용량 순으로 정렬하는 기능을 추가하여 사용 편의성을 증가시켜야 한다.

 마지막으로 앱을 완전히 종료해도 백그라운드에서 계속 모니터링을 수행할 수 있도록 해야 한다. 이 앱의 모니터링은 백그라운드에서 이루어지기 때문에 앱의 종료 이후에도 6~7시간 정도는 모니터링을 계속 수행할 수 있다. 하지만 종료된 이후 오랫동안 다시 실행시키지 않거나 아래 이미지처럼 안드로이드의 ‘어플 사용 기록’ 화면에서 ‘모두 지우기’ 버튼을 누르면 모니터링을 중단한다. 이는 OS가 자원 소모를 줄이기 위해 메모리에서 어플의 데이터를 삭제하기 때문인 것으로 보인다. 따라서 앱 데이터가 메모리에서 삭제되어도 백그라운드에서 다시 실행되도록 개선해야 한다.

<img src="https://user-images.githubusercontent.com/55964775/129677456-54267879-7f09-4d98-af35-8e36165e9d8c.png" alt="17" style="zoom:40%;" />

<br>

<br>

---


[^1]: 박지연. (2013). *스마트폰을 위한 사용자 이벤트기반의 이상 행동 탐지 기법* (석사학위). 서울대학교 전기·컴퓨터 공학부 대학원, 서울 

