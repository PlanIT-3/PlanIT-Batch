# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Language Preference
- **기본 응답 언어**: 한국어로 응답해주세요
- **Default Response Language**: Please respond in Korean
- **코드 주석**: 한국어로 작성
- **문서화**: 한국어로 작성

## Project Overview

PlanIT-Batch는 **PlanIT 금융 서비스의 배치 처리 시스템**입니다. **순수 Spring Framework 기반**으로 구축되어 **Spring Boot를 사용하지 않습니다**. Java 17과 Gradle을 사용하며, Spring Framework 5.3.37과 Spring Batch 4.3.10을 사용하여 예약된 데이터 처리 작업을 수행하는 독립 실행형 배치 처리 시스템입니다.

**프로젝트 관계:**
- **PLANIT_BE**: 메인 웹 애플리케이션 (Spring Framework 5.3.37, Java 17)
- **PlanIT-Batch**: 배치 처리 시스템 (Spring Framework 5.3.37 + Spring Batch 4.3.10, Java 17)
- 두 프로젝트 모두 **순수 Spring Framework** 사용 (**Spring Boot 없음**)
- 동일한 데이터베이스와 도메인 모델 공유

**핵심 아키텍처:**
- **Spring Framework**: 순수 Spring Framework 5.3.37 (Spring Boot 자동 설정 없음)
- **Spring Batch**: Enterprise 배치 처리 프레임워크 4.3.10 사용
- **수동 설정**: 모든 Bean과 설정을 Java Configuration(@Configuration, @Bean)으로 명시적 구성
- **Reader-Processor-Writer**: Spring Batch의 표준 패턴 적용

## Build and Development Commands

### Build System
```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

# Clean and rebuild
./gradlew clean build

# Run tests
./gradlew test
```

### Development Workflow
```bash
# Build without running tests (faster development builds)
./gradlew build -x test

# Check dependencies
./gradlew dependencies

# View all available tasks
./gradlew tasks
```

## Architecture Overview

### Core Framework Configuration (순수 Spring Framework)
- **Application Entry Point**: `BatchApplication.java` - Spring Boot 없이 AnnotationConfigApplicationContext를 직접 사용하여 애플리케이션 부트스트래핑
- **Configuration**: `AppConfig.java` - @EnableBatchProcessing, @EnableScheduling으로 배치 처리와 스케줄링 활성화, 전체 패키지 구조에 대한 컴포넌트 스캔 설정
- **Database Configuration**: `DatabaseConfig.java` - BatchConfigurer 인터페이스를 직접 구현하여 이중 DataSource 설정 (메인 애플리케이션 DB + 전용 배치 메타데이터 DB)
- **수동 설정**: Spring Boot의 자동 설정 대신 모든 Bean과 설정을 개발자가 직접 정의

### Data Layer Architecture
- **Dual Database Strategy**: 
  - Primary DataSource: Main application database (MySQL with HikariCP connection pooling)
  - Batch DataSource: Dedicated database for Spring Batch metadata tables
- **MyBatis Integration**: SqlSessionFactory configured to load mapper XML files from `classpath:mapper/*.xml`
- **Transaction Management**: Separate transaction managers for application and batch operations

### Batch Processing Structure
The batch architecture follows Spring Batch's standard Reader-Processor-Writer pattern:

```
batch/
├── job/          # Job definitions and configuration
├── reader/       # ItemReader implementations for data input
├── processor/    # ItemProcessor implementations for data transformation
├── writer/       # ItemWriter implementations for data output
└── scheduler/    # Scheduled job execution (@Scheduled annotations)
```

### Domain Layer
```
domain/
├── vo/           # Value Objects for data transfer
└── mapper/       # MyBatis mapper interfaces
```

### Key Design Patterns
- **Dependency Injection**: Spring Framework IoC container manages all components
- **Separation of Concerns**: Clear separation between batch processing logic and data access
- **Configuration Management**: Property-driven configuration with `application.properties`
- **Connection Pooling**: HikariCP for optimized database connections with leak detection

### Scheduling System
- **JobScheduler**: Spring's `@Scheduled` annotation for cron-based job execution
- **Example**: Currently configured with cron expression `"0 46 13 * * ?"` (daily at 13:46)
- **Job Launcher**: Spring Batch JobLauncher for programmatic job execution

## Database Configuration Requirements

**데이터베이스 설정** (application.properties에 구성 필요):
- `jdbc.driver`, `jdbc.url`, `jdbc.username`, `jdbc.password` - 메인 애플리케이션 데이터베이스 (PLANIT_BE와 공유)
- `batch.jdbc.driver`, `batch.jdbc.url`, `batch.jdbc.username`, `batch.jdbc.password` - Spring Batch 메타데이터 전용 데이터베이스

**PLANIT_BE 연동:**
- 동일한 MySQL 데이터베이스 스키마 사용
- Member, Account, Goal, Product 등 도메인 모델 공유
- PLANIT_BE에서 생성된 데이터를 배치에서 처리

## Technology Stack
- **Java 8** (source/target compatibility)
- **순수 Spring Framework 5.3.37** (Core, Web MVC, TX, JDBC) - **Spring Boot 사용 안 함**
- **Spring Batch 4.3.10** (Core, Infrastructure, Test)
- **MyBatis 3.4.6** with Spring integration
- **MySQL Connector 8.1.0**
- **HikariCP 2.7.4** for connection pooling - 수동 설정
- **Gradle 8.8** build system
- **JUnit 5** for testing
- **Lombok** for boilerplate reduction
- **Jackson** for JSON processing

**주의사항**: 
- Spring Boot의 자동 설정, 스타터 의존성, application.yml/properties 자동 로딩 등은 사용하지 않음
- 모든 설정을 @Configuration 클래스에서 수동으로 구성
- Properties 파일은 @PropertySource로 수동 로딩

## 배치 작업 우선순위

### 🔥 1차 개발 우선순위: 예적금 관련 배치

#### 1.1 예적금 계좌 정보 수집 배치
- **목적**: CODEF API를 통한 예적금 계좌 정보 수집 및 동기화
- **데이터 소스**: CODEF API (은행 계좌 정보)
- **실행 주기**: 매일 자정
- **처리 대상**: 사용자의 모든 예적금 계좌 잔액, 이자율 정보

#### 1.2 예적금 세금 계산 배치
- **목적**: 예적금 수익에 대한 세금 계산 (이자소득세 15.4%)
- **계산 로직**: 예적금 수익 × 15.4% (소득세 14% + 지방소득세 1.4%)
- **실행 주기**: 월간 (매월 1일)
- **저장 데이터**: 월별/연별 세금 부담액, 실수익

#### 1.3 예적금 수익률 분석 배치
- **목적**: 예적금 상품별 수익률 분석 및 비교
- **분석 내용**: 상품별 연간 수익률, 복리 계산, 만기 예상 수익
- **실행 주기**: 주간 (매주 일요일)
- **저장 데이터**: 상품별 수익률 순위, 추천 점수

### 🔄 2차 개발 예정: ISA 및 기타 배치

#### 2.1 ETF Daily 배치 (향후 구현)
- **목적**: ETF 가격 정보 일일 수집 및 업데이트
- **데이터 소스**: OpenData API (ETF 가격 정보)
- **실행 주기**: 매일 오후 (장 마감 후)
- **처리 대상**: 모든 ETF 상품의 일일 가격, 수익률 데이터

#### 2.2 ISA 계좌 분석 배치 (향후 구현)

#### 2.1 일반 세금 그래프 배치 (General Tax Graph Batch)
- **목적**: ISA 계좌와 예적금 계좌의 세금 부담 차이를 계산하여 월/연별 세금 비교 그래프 생성
- **실행 조건**: 사용자가 ISA 계좌와 예적금 계좌를 각각 하나 이상 보유
- **처리 흐름**:
  1. **계좌 정보 수집**: 세금비교서비스를 통해 ISA 계좌와 예적금 계좌 정보 수집
  2. **수익 계산**: 월/연간 수익률 기준으로 세금 부담 차이 계산
  3. **그래프 구성**: 비교 데이터를 그래프 형태로 구성
  4. **DB 저장**: 계산된 그래프 데이터를 데이터베이스에 저장

#### 2.2 세금 비교 계산 로직
```
예적금 세금: 수익 × 15.4% (이자소득세)
ISA 세금: 
- 200만원 이하: 0% (비과세)
- 200만원 초과: 초과분 × 9.9%

절세 효과 = 예적금 세금 - ISA 세금
```

#### 2.3 기타 ISA 분석 배치
- **ISA vs 일반 비교 그래프**: ISA 계좌와 일반 투자 계좌 수익률 비교 분석
- **누적 절세 그래프**: 시간별 누적 세금 절약 효과 계산
- **ISA 계좌 + 세금**: ISA 계좌의 세금 혜택 상세 계산
- **연간 절세 금액/절세율 요약**: 년간 절세 효과 요약 리포트 생성

### 3. 투자 분석 및 리포트 생성 배치
- **수익률 막대 그래프**: 각 투자 상품별 수익률 시각화 데이터 생성
- **투자금 총액**: 사용자별 총 투자 현황 집계 및 업데이트
- **투자 제언**: AI 기반 개인별 투자 추천 알고리즘 실행
- **권장 투자금액 그래프**: 최적 포트폴리오 배분 계산
- **목표 그래프**: 개인별 목표 달성 진행률 분석 및 업데이트

### 4. 리밸런싱 알림 배치
- **목적**: 포트폴리오 리밸런싱 필요성 검토 및 알림 생성
- **분석 대상**: ISA 계좌, 예금 상품의 목표 대비 현재 배분 비율
- **알림 조건**: 목표 배분 대비 ±5% 이상 차이 발생 시
- **실행 주기**: 주간 단위

## 배치 스케줄링 계획

### 1차 개발: 예적금 배치 스케줄링

#### 일일 배치 (Daily Jobs)
- **예적금 계좌 정보 수집** - 매일 자정 (CODEF API 호출)

#### 주간 배치 (Weekly Jobs)
- **예적금 수익률 분석** - 매주 일요일 오후 2시
- **예적금 상품 비교 리포트** - 매주 월요일 오전 9시

#### 월간 배치 (Monthly Jobs)
- **예적금 세금 계산** - 매월 1일 오전 10시
- **예적금 만기 알림** - 매월 1일 오후 2시

### 2차 개발 예정: 전체 배치 스케줄링

#### 일일 배치 (Daily Jobs) - 향후 구현
- **ETF 가격 업데이트** - 매일 오후 4시 (장 마감 후)
- **계좌 잔액 동기화** - 매일 자정
- **일일 수익률 계산** - 매일 오후 5시

#### 주간 배치 (Weekly Jobs) - 향후 구현
- **리밸런싱 알림 생성** - 매주 일요일 오후 8시
- **주간 투자 리포트** - 매주 월요일 오전 9시
- **포트폴리오 분석** - 매주 토요일 오후 2시

#### 월간 배치 (Monthly Jobs) - 향후 구현
- **월간 절세 효과 계산** - 매월 1일 오전 10시
- **투자 목표 진행률 업데이트** - 매월 1일 오후 2시
- **개인별 투자 제언 생성** - 매월 15일 오전 11시

## 배치 작업 구현 가이드

### 1차 개발: 예적금 배치 Reader-Processor-Writer 패턴

```
예적금 계좌 정보 수집 배치:
├── DepositAccountReader: CODEF API를 통한 예적금 계좌 정보 읽기
├── DepositAccountProcessor: 계좌 정보 검증 및 변환
└── DepositAccountWriter: 계좌 정보를 데이터베이스에 저장/업데이트

예적금 세금 계산 배치:
├── DepositIncomeReader: 예적금 계좌별 수익 데이터 읽기
├── DepositTaxProcessor: 세금 계산 (수익 × 15.4%)
│   ├── 이자소득 계산
│   ├── 소득세 계산 (14%)
│   ├── 지방소득세 계산 (1.4%)
│   └── 실수익 계산 (총수익 - 세금)
└── DepositTaxWriter: 세금 계산 결과를 데이터베이스에 저장

예적금 수익률 분석 배치:
├── DepositProductReader: 예적금 상품 정보 읽기
├── DepositRateProcessor: 수익률 분석 및 순위 계산
│   ├── 연간 수익률 계산
│   ├── 복리 계산
│   ├── 만기 예상 수익 계산
│   └── 상품별 추천 점수 계산
└── DepositAnalysisWriter: 분석 결과를 데이터베이스에 저장
```

### 2차 개발 예정: 전체 배치 Reader-Processor-Writer 패턴
```
ETF 가격 업데이트 배치 (향후 구현):
├── ETFPriceReader: OpenData API에서 ETF 가격 데이터 읽기
├── ETFPriceProcessor: 가격 변동률, 수익률 계산
└── ETFPriceWriter: 계산된 데이터를 데이터베이스에 저장

ISA vs 예적금 세금 비교 배치 (향후 구현):
├── AccountReader: ISA 계좌와 예적금 계좌 정보 읽기
├── TaxComparisonProcessor: 세금 부담 차이 계산 및 그래프 데이터 생성
└── TaxGraphWriter: 그래프 데이터를 데이터베이스에 저장
```

## Current State
프로젝트는 초기 설정 단계로 템플릿 클래스들(Temp*)이 있으며, 위의 리밸런싱 배치 명세에 따라 실제 비즈니스 로직을 구현해야 합니다.

**개발 시 참고사항:**
- PLANIT_BE의 도메인 모델 구조 참고: Member, Account, Goal, Product, Action
- PLANIT_BE의 Mapper XML 파일 구조 참고 (src/main/resources/mapper/)
- PLANIT_BE의 외부 API 연동 유틸리티 활용: CodefAccountUtil, OpenApiUtil
- 동일한 데이터베이스 접근 패턴과 MyBatis 설정 활용