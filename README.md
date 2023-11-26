# 기본 조인
---
- join()
    - 첫 번쨰 파라미터에 조인 대상을 두 번쨰 파라미터에 별칭(alias)으로 사용할 Q 타입을 지정.
    - join(조인 대상 , 별칭으로 사용할 Q타입)
    
    ```java
    .join(member.team, team)
    ```
    
    Query
    
    ```java
    (inner 생략)join
        team t1_0 on t1_0.id=m1_0.team_id
    ```
    

### 패치 조인

- 먼저 **`join()`**과 **`fetchJoin()`**의 차이를 살펴보자. **`join()`**은 연관된 컬럼 값이 있는 경우에만 출력하며, 없으면 출력하지 않는 특징이 있다.
- 회원과 팀 컬렉션을 조인 했을 경우 연관된 컬렉션의 값이 같이 조회 될 것으로 기대해서는 안 된다. 밑에가 하나의 예시이다.

### JPQL 문법

```java
select t
from Member m
inner join Team t
on t.id = m.team_id
where(t.name = "Team_A")
```

### QueryDSL Join 문법

```java
query
     .selectFrom(member)
     .join(member.team , team)
     .where(member.username.eq("member1"))
     .fetchOne();
```

- Query

```java
Hibernate: 
    select
        m1_0.id,
        m1_0.age,
        m1_0.team_id,
        m1_0.username 
    from
        member m1_0 
    join
        team t1_0 
            on t1_0.id=m1_0.team_id 
    where
        m1_0.username=?
```

- 회원은 팀과 조인이 되어있지만, 결과로는 회원의 쿼리만 출력되며 팀의 쿼리는 출력되지 않습니다. **JPQL은 결과를 반환할 때 연관관계를 고려하지 않습니다**.

### fetch join()

- 패치 조인은 조인과 반대로 연관된 엔티티를 한번 함께 조회한다.
- fetch join()에 기본 전략은  **글로벌 로딩**이라 부른다.
    - fetch = FetchType.LAZY 을 글로벌 로딩 전략이라고 부른다.
- fetch join 은 JPA 문법이라는 점 주의.

### JPQL 문법

```java
select t
from Member m
inner join fetch t.members 
where(t.name = "Team_A")
```

### QueryDSL fetch join() 문법

```java
query
     .selectFrom(member)
     .join(member.team , team).fetchJoin()
     .where(member.username.eq("member1"))
     .fetchOne();
```

- Query

```java
Hibernate: 
    select
        m1_0.id,
        m1_0.age,
        t1_0.id,
        t1_0.name,
        m1_0.username 
    from
        member m1_0 
    join
        team t1_0 
            on t1_0.id=m1_0.team_id 
    where
        m1_0.username=?
```

- **`fetch join`**은 성능 최적화 및 N+1 쿼리 문제를 해결하기 위해 필요한 시점에 연관된 엔터티를 효율적으로 불러오기 위해 사용됩니다.

### 사용 목적

- 지연 로딩을 사용하고 최적화가 필요한 시점에 알맞게 fetch join 을 사용하는것을 추천.
- 필요한 시점에 최적화를 위해, 특히 지연 (LAZY)로딩으로 인한 N+1 쿼리 문제를 해결하기 위해, **`fetch join`**을 사용할 수 있습니다. **`fetch join`은 쿼리 시점에 연관된 엔터티를 함께 로딩하므로 지연 로딩이 발생하지 않습니다.**


### 페치 조인 대상에는 별칭을 줄수없다.

```java
.join(member.team , team).fetchJoin()
```

- fetchJoin() 보면 따로 별칭을 줄수있는 부분이 없다.
