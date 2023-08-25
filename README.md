# Iluvit Backend Team
> 어린이집/유치원 정보 모두 모아 보고싶다면? <br>
> [![play store badge](http://img.shields.io/badge/Play%20Store-414141?style=flat-square&logo=google-play&link=https://play.google.com/store/apps/details?id=org.sopt.havit)](https://play.google.com/store/apps/details?id=com.iluvit.app&hl=ko-KR) 
> [![app store badge](http://img.shields.io/badge/App%20Store-0D96F6?logoColor=white&style=flat-square&logo=appstore&link=https://apps.apple.com/us/app/havit/id1607518014)](https://apps.apple.com/kr/app/%EC%95%84%EC%9D%B4%EB%9F%AC%EB%B9%97/id6450625509)

<br>

<p align="center"><img src="https://github.com/FISOLUTION/iluvit-backend/assets/78267146/c552cde8-7241-48dd-81e9-4dafd99d23b0" height=50></p>
<p align="center"><img src="https://github.com/FISOLUTION/iluvit-backend/assets/78267146/a8557742-9395-421d-b975-c8bb6be9e547" height=70></p>

<br>

![스크린샷 2022-09-08 오후 2 23 43](https://user-images.githubusercontent.com/65563854/189040932-c19ec8f9-06a0-45d0-bc8e-d06b8cadb1a2.png)
![스크린샷 2022-09-08 오후 2 24 55](https://user-images.githubusercontent.com/65563854/189041068-127f96a1-2780-492a-b056-fa0433a91888.png)
![스크린샷 2022-09-08 오후 2 25 13](https://user-images.githubusercontent.com/65563854/189041112-844af4d3-5549-41e3-95cb-741c93e54cb6.png)
![스크린샷 2022-09-08 오후 2 25 30](https://user-images.githubusercontent.com/65563854/189041137-e20d5278-fbbd-48f2-b545-b9219e09bf08.png)

## API Document
- [API 명세서 노션](https://half-turn-bb0.notion.site/API-ILUVIT-4f0bd47ebe8c43f1ab3ec475389b3898?pvs=4)
<br><br>

## Commit Convention
`ex ) git commit -m "[FEAT] 회원가입 기능 완료 #1"`
- `[CHORE]` : 동작에 영향 없는 코드, 작은 수정
- `[FEAT]` : 새로운 기능 구현
- `[ADD]` : Feat 이외의 부수적인 코드 추가, 라이브러리 추가, 새로운 파일 생성
- `[FIX]` : 버그, 오류 해결
- `[DEL]` : 쓸모없는 코드 삭제
- `[DOCS]` : README나 WIKI 등의 문서 수정
- `[MOVE]` : 프로젝트 내 파일이나 코드의 이동
- `[RENAME]` : 파일 이름 변경시
- `[REFACTOR]` : 전면 수정
- `[MERGE]`: 다른 브랜치와 병합
- `[STYLE]`: 코드가 아닌 스타일 변경을 하는 경우 or 주석 추가
- `[INIT]`: Initial Commit을 하는 경우

<br><br>

## Branch Strategy
- `[develop branch]` : prod 서버 배포 단위 branch
- `[release branch]` : dev 서버 배포 단위 branch
- `[develop branch]` : 주요 개발 branch, main merge 전 거치는 branch
- `[feature branch]` : 기능별 branch

```
1. Issue를 생성한다.
2. feature Branch를 생성한다. ex) feature/#issue-num
3. Add - Commit - Push - Pull Request 의 과정을 거친다.
4. Pull Request가 작성되면 작성자 이외의 다른 팀원이 Code Review를 한다.
5. Code Review가 완료되면 Pull Request 작성자가 develop Branch로 merge 한다.
6. merge된 작업이 있을 경우, 다른 브랜치에서 작업ㅠㄱ>을 진행 중이던 개발자는 본인의 브랜치로 merge된 작업을 Pull 받아온다.
7. 종료된 Issue와 Pull Request의 Label과 Project를 관리한다.
```

<br><br>

## ERD
![아이러빗 ERD](https://github.com/FISOLUTION/iluvit-backend/assets/78267146/d20a9b5d-7f18-4a83-81da-4b97064a9cc4)

<br><br>

## Architecture
![아이러빗 아키텍쳐](https://github.com/FISOLUTION/iluvit-backend/assets/78267146/c62c3764-988a-4922-9b01-b59a67d8a5e4)


<br><br>

## Tech Stack
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=SpringBoot&logoColor=white"/> <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=flat&logo=SpringSecurity&logoColor=white"/> <img src="https://img.shields.io/badge/AmazonEC2-FF9900?style=flat&logo=AmazonEC2&logoColor=white"/> <img src="https://img.shields.io/badge/NGINX-009639?style=flat&logo=NGINX&logoColor=white"/>
<img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=flat&logo=JSONWebTokens"/> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=flat&logo=Amazon S3&logoColor=white"/>
<img src="https://img.shields.io/badge/GitHub Actions-2088FF?style=flat&logo=GitHub Actions&logoColor=white"/> <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white"/> <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=flat&logo=Amazon RDS&logoColor=white"/>
<br><br>


## Contributors
| name |position|github|period| 
|------|---|---|---|
| 현승구  | BackEnd | https://github.com/digda5624 | 2021.06 ~ 2022.12 |
| 이승범  | BackEnd | https://github.com/sbl133 | 2022.01 ~ 2022.09 |
| 이창윤  | BackEnd | https://github.com/rooni97 | 2022.01 ~ 2022.12 |
| 박찬희  | BackEnd | https://github.com/cksgml12345 | 2022.03 ~ |
| 최민아  | BackEnd | https://github.com/minah9999 | 2022.06 ~ 2022.10 |
| 이서우  | BackEnd | https://github.com/dltjdn | 2023.01 ~ |
| 이영은  | BackEnd | https://github.com/ye0ngeun | 2023.01 ~ |

<br><br>

## Release Note
> 2022-09-08
>
Readme 파일 생성
> 2023-07-21

AOS version 1.0.0 배포
