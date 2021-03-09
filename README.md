# CrizenSolution_CrawlingWebsite
크리젠솔루션 인턴기간중에 완료했던 프로젝트입니다.
동적웹페이지인 네이버부동산을 Selenium과 Jsoup을 이용하여 필요한 정보를 크롤링하여 동적테이블로 정리하여 웹으로 로컬에 구현했습니다.
동적테이블은 html에서 javascript로 구현하였습니다.


Selenium의 문제인 시간이 너무 오래걸린다는 것을 해결하기위해 코드를 재정비 하였습니다.

단지 코드 1525의 경우 21초 -> 15초 로 약 6초 를 절약하는데에 성공하였습니다.
절약 방법으로는 최대한 Selenium을 이용한 웹 구동을 줄이고 Jsoup으로 받아서 크롤링하는식으로 바꾸었고, Thread.sleep()을 사용하지 않고, 다음 대상이 나타날떄까지만 기다릴수있도록 
wait.until(ExpectedConditions.presenceOfElementLocated(By.id(""))).click(); -> 를 이용하였습니다.

최종적으로 약 30%정도 속도향상을 하였습니다.







"리얼 서치" 라는 웹사이트의 구현의 일부분을 기여하였습니다.


단지의 정보를 면적별로 정리한 동적 테이블입니다.
<img width="1637" alt="스크린샷 2021-03-09 오후 9 37 04" src="https://user-images.githubusercontent.com/73048180/110471553-993fda00-811f-11eb-9906-20fcea3ed5bc.png">
<img width="1549" alt="스크린샷 2021-03-09 오후 9 37 19" src="https://user-images.githubusercontent.com/73048180/110471580-a230ab80-811f-11eb-9628-3892fcea1e6f.png">

그 면적에해당하연 모든 기능들을 한곳에 모아 정리한 동적 테이블입니다. 

<img width="1596" alt="스크린샷 2021-03-09 오후 9 37 32" src="https://user-images.githubusercontent.com/73048180/110471605-aa88e680-811f-11eb-96bf-32256e9cc0f2.png">
<img width="1487" alt="스크린샷 2021-03-09 오후 9 37 46" src="https://user-images.githubusercontent.com/73048180/110471624-b2488b00-811f-11eb-8674-7da3e722ced6.png">

 
