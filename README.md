# Simple-Currency-calculation(간단한 환율 계산)

## 프로젝트 소개
간단한 환율 계산기이다. 미국 금액(USD)를 기준으로 한국(KRW), 일본(JPY) 필리핀(PHP) 국가들의 금액으로 환율된 금액을 보여준다.

## 프로젝트 기술
- Kotlin
- Retrofit2
- 환율api( https://currencylayer.com/ )

## 주요기능
- 송금국가는 미국 통화로 고정.(USD)
- 수취국가를 선택하면 아래 환율금액이 계산되어 보여준다. 환율은 1 USD 기준으로 각각 KRW, JPY, PHP의 대응 금액입니다.
- 송금액을 USD로 입력하면 아래 수취금액이 KRW, JPY, PHP 중 하나로 계산됩니다.
- 환율과 수취금액은 소숫점 2째자리까지, 3자리 이상 되면 콤마를 찍는다.
- 환율은 수취국가가 변경되거나, 송금액을 입력할때마다 API로 서버에 요청해서 새로운 환율 정보를 가져옵니다.
- 수취금액이 10,000 USD보다 크면 에러메시지 출력.

## 프로젝트 이미지
![환율이미지1](https://user-images.githubusercontent.com/58352779/82043035-db59e400-96e5-11ea-8aee-cde1c8d5b101.PNG)
