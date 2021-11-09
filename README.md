# CrizenSolution_CrawlingWebsite
## Introduce Project

  * 크리젠솔루션 인턴기간중에 완료했던 프로젝트이다.
  * 동적웹페이지인 네이버부동산을 Selenium과 Jsoup을 이용하여 필요한 정보를 Crawling후 Json 형태로 데이터 생성.

  * Spring boot 를 이용하여 백엔드 개발을 진행.

  * 동적테이블을 만들기위해 HTML,JavaScript를 이용하여 프론트앤드 개발 진행.
  * 데이터들을 동적테이블로 정리하여 웹으로 로컬에 구현.


## How i made this code efficiently

  * Selenium의 문제인 시간이 너무 오래걸린다는 것을 해결하기위해 코드를 재정비 함.

  * 단지 코드 1525의 경우 21초 -> 15초 로 "약 6초" 절약성공.
  * 시간절약 방법의 첫번째로는, 최대한 Selenium을 이용한 웹 구동을 줄이고 Jsoup으로 받아서 Crawling하는식으로 바꿈. 이는 웹피이지의 동적 컨트롤을 최소화하기 때문에 시간절약하기에 유리하다.

  * 두번째 방법으로는 , Thread.sleep()을 사용하지 않고, 다음 대상이 나타날때까지만 기다릴수있도록 wait.until(ExpectedConditions.presenceOfElementLocated(By.id(""))).click(); -> 를 이용.

  * 최종적으로 약 30%의 속도가 향상됨.





"리얼 서치(Real Search)" 웹사이트의 구현의 일부분을 기여하였습니다.

## Result

- 단지의 정보를 면적별로 정리한 동적 테이블.
<img width="1637" alt="스크린샷 2021-03-09 오후 9 37 04" src="https://user-images.githubusercontent.com/73048180/110471553-993fda00-811f-11eb-9906-20fcea3ed5bc.png">
<img width="1549" alt="스크린샷 2021-03-09 오후 9 37 19" src="https://user-images.githubusercontent.com/73048180/110471580-a230ab80-811f-11eb-9628-3892fcea1e6f.png">

- 그 면적에 해당하는 모든 정보들을 한곳에 모아 정리한 동적 테이블. 

<img width="1596" alt="스크린샷 2021-03-09 오후 9 37 32" src="https://user-images.githubusercontent.com/73048180/110471605-aa88e680-811f-11eb-96bf-32256e9cc0f2.png">
<img width="1487" alt="스크린샷 2021-03-09 오후 9 37 46" src="https://user-images.githubusercontent.com/73048180/110471624-b2488b00-811f-11eb-8674-7da3e722ced6.png">

**일일이 네이버부동산의 매물을 확인하고 입력할 필요없이 입력과 클릭한번으로 부동산 매물의 모든 정보를 확인 할 수 있게 되었다.**
