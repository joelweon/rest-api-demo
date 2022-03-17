스프링 부트 버전을 올리면 바뀔 수 있는 일 ● 기본 (자동) 설정 값 변경 ● 의존성 변경 ● 기존 애플리케이션의 동작이 바뀌거나 컴파일 에러가 발생할 수 있다.

스프링 부트 2.2.* 주요 변화 ● JUnit 4 -> JUnit 5 ● 스프링 HATEOAS 버전이 올라가면서 스프링 HATEOAS의 API가 바뀌었다.

스프링 HATEOAS ● Resource -> EntityModel ● Resources -> CollectionModel ● PagedResrouces -> PagedModel ● ResourceSupport ->
RepresentationModel ● assembler.toResource -> assembler.toModel ● org.springframework.hateoas.mvc.ControllerLinkBuilder
-> org.springframework.hateoas.server.mvc.WebMvcLinkBuilder ● MediaTypes 중에 (UTF8)인코딩이 들어간 상수 제거.

JUnit 5 ● org.junit -> org.junit.jupiter ● @Ignore -> @Disabled ● @Before, @After -> @BeforeEach, @AfterEach ●
@TestDescription (우리가 만든거) -> @DisplayName

스프링 MVC 변경 ● MediaType 중에 (UTF8)인코딩이 들어간 상수 deprecation.
