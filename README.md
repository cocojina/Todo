
# 일정 관리 시스템 (Calendar Todo)

달력 기반의 일정 관리 웹 애플리케이션입니다. 직관적인 UI로 일정을 쉽게 관리할 수 있습니다.

## 주요 기능

### 1. 달력 뷰
- 월간 달력 형태로 일정 조회
- 각 날짜별 할 일 목록 미리보기
- 오늘 날짜 하이라이트 표시
- 이전/다음 달 이동

### 2. 할 일 관리
- 날짜별 할 일 목록 조회
- 할 일 추가/수정/삭제
- 완료/미완료 상태 토글
- 월별 할 일 일괄 삭제

### 3. 통계
- 월간 완료/미완료 할 일 수 집계
- 사이드바에서 실시간 확인

## 기술 스택

- **Backend**
  - Java 17
  - Spring Boot 3.x
  - JPA/Hibernate
  - H2 Database

- **Frontend**
  - Mustache Template Engine
  - Bootstrap 5
  - JavaScript


## 1. 웹 브라우저에서 접속
```
http://localhost:8080/calendar
```

## API 엔드포인트

### Calendar
- `GET /calendar` - 달력 화면 표시
- `GET /calendar?year={year}&month={month}` - 특정 연월의 달력 조회

### Todo
- `GET /todos?date={date}` - 특정 날짜의 할 일 목록 조회
- `POST /todos/add` - 새로운 할 일 추가
- `POST /todos/toggle/{id}` - 할 일 완료/미완료 토글
- `POST /todos/delete/{id}` - 할 일 삭제
- `POST /todos/update/{id}` - 할 일 수정
- `POST /todos/deleteAllMonth` - 해당 월의 모든 할 일 삭제

## 데이터베이스

- H2 인메모리 데이터베이스 사용
- 파일 기반 저장으로 데이터 영속성 보장
- 개발 환경에서 `localhost:8080/h2-console`로 접근 가능
