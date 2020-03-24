# Welcome to YouHi! 


![KakaoTalk_20200316_012625046](https://user-images.githubusercontent.com/43363576/76705711-435f6e80-6725-11ea-92ca-9860ca88fb0a.png)


## 1. 프로젝트 소개

본 프로젝트는 현 YouTube의 미성년자 시청 불가 영상에 대한 필터링 시스템의 단점을 보완하는 것을 목표로 한다. 현재 YouTube의 영상 검열 시스템은 사용자의 신고 혹은 운영자의 모니터링을 통한 수동 검열로 이루어진다. 이러한 방식은 새로 업로드된 영상을 즉각적으로 필터링 할수 없으며 사용자의 신고를 받거나 운영자의 모니터링에 의해 검열 되기 전까지 청소년들에게 무방비 상태로 노출된다. 이를 방지하기 위하여 영상이 업로드되기 전에 사전 필터링을 하여 가이드라인(YouTube Community GuideLine, 방송통신심의위원회)에 위배되는 내용이 영상에 포함되어 있을 경우, 청소년 시청 불가 컨텐츠로 분류한다. 사전 필터링 과정을 통해 청소년에게 유해한 컨텐츠가 노출되는 것을 예방한다.
   
**우리 프로젝트의 최종 목표는 다양한 동영상 플랫폼에서 우리의 시스템을 적극 활용하여, 필터링 작업을 거치치 않는다면 업로드할 수 없도록 하여 미성년자들에게 부적합한 영상을 시청하지 못하게 하는 것이다.**

### 영상 필터링
* 폭력적인 장면
  * 영화, 애니메이션 등에서 인간형 피부에 잔인하게 흉기에 공격당하는 장면이 명확하게 드러나는 경우 
  * 게임에서 총 또는 흉기로 인간형 생명체를 잔인하게 공격하는 장면이 명확하게 드러나는 경우
* 선정적인 장면
  * 여성 혹은 남성의 성기가 노출되는 장면이 명확하게 드러나는 경우
  * 여성의 나체 또는 속옷외에 어떠한 옷을 입지않고 노출되는 장면이 명확하게 드러나는 경우
* 유해한 장면
  * 담배를 장시간동안 흡연하거나, 담배를 주제로 한 영상인 경우
   
### 음성 필터링
* 저속한 언어
   * 비속어가 어떠한 필터링없이 명확하게 발음되는 경우      

   
_필터링 기준은 다음과 같이 [YouTube 이용정책 - 연령별 제한 등급](https://support.google.com/youtube/answer/2802167?hl=ko)을 참고하였다._   

![guide_line](https://user-images.githubusercontent.com/43363576/76705513-de574900-6723-11ea-89ee-8fc098de78bb.png)



## 2. 소개 영상

프로젝트 소개하는 영상을 추가하세요

## 3. 팀 소개


   ### Professor 임은진 교수님



   ### 이태훈 

   ![Lee_Tae_Hoon](https://user-images.githubusercontent.com/43363576/76700751-f025f580-66fd-11ea-800f-beb32b98a1d9.jpg)
  
<pre>메일: says7869@gmail.com
역할: Video Classification 모델링과 학습 및 AWS 서버를 이용한 전체적인 소프트웨어 설계
</pre>

   ### 이인평

   ![Lee_In_Pyeong](https://user-images.githubusercontent.com/43363576/76700753-f0be8c00-66fd-11ea-8d2c-e914ac913b4f.jpg)

<pre>메일: jinipyung@gmail.com
역할: Video Classfication, FastText 학습을 위한 데이터셋 구축 및 FastText 모델링과 학습
</pre>

   ### 이주형

   ![Lee_Ju_Hyeong](https://user-images.githubusercontent.com/43363576/76700749-eef4c880-66fd-11ea-9b6e-71b7a2d99c96.jpg)

<pre>메일: srlee96@kookmin.ac.kr
역할: STT(Speech To Text) API 적용 및 Video Classification 데이터셋 구축
</pre>

   ### 김성수
   
   ![Kim_Sung_Soo](https://user-images.githubusercontent.com/43363576/76728656-bb1bb080-679a-11ea-8124-5d4e078fa880.jpg)
   
<pre>메일: 
역할: 웹페이지와 웹서버 구축
</pre>

   ### 김민재

   ![Kim_Min_Jae](https://user-images.githubusercontent.com/43363576/76700752-f025f580-66fd-11ea-9a67-8fd9e8231f06.jpg)

<pre>메일: minjae103030@naver.com
역할: Video Classfication, FastText 학습을 위한 데이터셋 구축 및 웹페이지 디자인 UI 제작
</pre>

## 4. 사용법

소스코드제출시 설치법이나 사용법을 작성하세요.

## 5. Abstract

Our Project's goal is to prevent people who watch youtube or video platform website from watching a nasty videos. So we have to filter about video and voice. Video filter is filtered from CRNN DeepLearning Model. The scene we are trying to filter is either sensational or violent. We also want to include scenes of alchol, arug, and cigarettes. And then voice filter  is filtered through two processes. First we have to convert voice to text by using STT(Speech To text) technology. Second, use fasttext to detect whether the translated text contains swear words. After filtering, If any one is detected, uploading is restricted. Also we tell you
