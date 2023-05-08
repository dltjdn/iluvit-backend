package FIS.iLUVit;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.*;
import FIS.iLUVit.domain.reports.Report;
import FIS.iLUVit.domain.reports.ReportDetailComment;
import FIS.iLUVit.domain.reports.ReportDetailPost;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Creator {

    public static Parent createParent(String phoneNum){
        return Parent.builder()
                .id(-1L)
                .nickName("nickName")
                .loginId("loginId")
                .password("pwd")
                .phoneNumber(phoneNum)
                .emailAddress("asd@asd")
                .name("name")
                .address("address")
                .detailAddress("detailAddress")
                .build();
    }

    public static Parent createParent(Long userId){
        return Parent.builder()
                .id(userId)
                .nickName("nickName")
                .loginId("loginId")
                .password("pwd")
                .phoneNumber("dfdsf")
                .emailAddress("asd@asd")
                .name("name")
                .address("address")
                .detailAddress("detailAddress")
                .build();
    }

    public static Parent createParent(String name, String phoneNum){
        return Parent.builder()
                .phoneNumber(phoneNum)
                .name(name)
                .build();
    }

    public static Parent createParent(Long userId, String phoneNum, String loginId, String nickname) {
        return Parent.builder()
                .id(userId)
                .nickName(nickname)
                .loginId(loginId)
                .password("pwd")
                .phoneNumber(phoneNum)
                .emailAddress("asd@asd")
                .name("name")
                .address("address")
                .detailAddress("detailAddress")
                .build();
    }

    public static Kindergarten createKindergarten(Long id, Area area, String name, Theme theme, Integer minAge, Integer maxAge, String addInfo, String program, BasicInfra basicInfra){
        return Kindergarten.kBuilder()
                .id(id)
                .area(area)
                .name(name)
                .theme(theme)
                .minAge(minAge)
                .maxAge(maxAge)
                .addInfo(addInfo)
                .program(program)
                .basicInfra(basicInfra)
                .build();
    }

    public static Kindergarten createKindergarten(Area area, String name, Theme theme, Integer minAge, Integer maxAge, String addInfo, String program, BasicInfra basicInfra, Integer score){
        return Kindergarten.kBuilder()
                .area(area)
                .name(name)
                .theme(theme)
                .minAge(minAge)
                .maxAge(maxAge)
                .addInfo(addInfo)
                .program(program)
                .basicInfra(basicInfra)
                .score(score)
                .build();
    }

    public static Kindergarten createKindergarten(String name){
        return Kindergarten.kBuilder()
                .name(name)
                .build();
    }

    public static Area createArea(String sido, String sigungu){
        return new Area(sido, sigungu);
    }



    public static Theme createTheme(Boolean english, Boolean foreigner, Boolean clean, Boolean buddhism, Boolean christianity, Boolean catholic, Boolean animal, Boolean plant, Boolean camping, Boolean nature, Boolean art, Boolean music, Boolean math, Boolean sport, Boolean coding, Boolean manner, Boolean genius){
        return new Theme(english, foreigner, clean, buddhism, christianity, catholic, animal, plant, camping, nature, art, music, math, sport, coding, manner, genius);
    }

    public static BasicInfra createBasicInfra(Boolean hasBus, Boolean hasPlayground,  Boolean hasCCTV, Boolean hasSwimPool, Boolean hasBackpack, Boolean hasUniform, Boolean hasKidsNote, Boolean hasHandWriteNote, Boolean hasPhysics, Integer busCnt, Integer buildingYear, Integer cctvCnt){
        return new BasicInfra(hasBus, hasPlayground, hasCCTV, hasSwimPool, hasBackpack, hasUniform, hasKidsNote, hasHandWriteNote, hasPhysics, busCnt, buildingYear, cctvCnt);
    }

    public static Post createPost(Long id, String title, String content, Boolean anonymous, Board board, User user) {
        return Post.builder()
                .id(id)
                .title(title)
                .content(content)
                .anonymous(anonymous)
                .commentCnt(0)
                .heartCnt(0)
                .imgCnt(0)
                .videoCnt(0)
                .board(board)
                .user(user)
                .comments(new ArrayList<>())
                .postHearts(new ArrayList<>())
                .anonymousOrder(3)
                .build();
    }

    public static Post createPost(String title, String content, Boolean anonymous, Integer commentCnt, Integer heartCnt, Integer imgCnt, Integer videoCnt, Board board, User user) {
        return new Post(title, content, anonymous, commentCnt, 0, heartCnt, imgCnt, videoCnt, board, user);
    }

    public static AuthNumber createAuthNumber(String phoneNum, String authNum, AuthKind authKind, LocalDateTime time) {
        return AuthNumber.builder()
                .phoneNum(phoneNum)
                .authKind(authKind)
                .authNum(authNum)
                .authTime(time)
                .build();
    }

    public static Post createPost(String title, String content, Boolean anonymous, Board board, User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .anonymous(anonymous)
                .commentCnt(0)
                .heartCnt(0)
                .imgCnt(0)
                .videoCnt(0)
                .board(board)
                .user(user)
                .postHearts(new ArrayList<>())
                .build();
    }

    public static ChatRoom createChatRoom(Long id, User receiver, User sender, Post post) {
        return ChatRoom.builder()
                .id(id)
                .receiver(receiver)
                .sender(sender)
                .post(post)
                .build();
    }

    public static ChatRoom createChatRoom(User receiver, User sender, Post post) {
        return new ChatRoom(receiver, sender, post, true);
    }

    public static Chat createChat(Long id, String message, ChatRoom chatRoom, User receiver, User sender) {
        return Chat.builder()
                .id(id)
                .date(LocalDate.now())
                .time(LocalTime.now())
                .message(message)
                .chatRoom(chatRoom)
                .receiver(receiver)
                .sender(sender)
                .build();
    }

    public static Chat createChat(String message, ChatRoom chatRoom, User receiver, User sender) {
        return Chat.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .message(message)
                .chatRoom(chatRoom)
                .receiver(receiver)
                .sender(sender)
                .build();
    }

    public static Teacher createTeacher(Long id, String name, Center center, Auth auth, Approval approval) {
        return Teacher.builder()
                .id(id)
                .name(name)
                .center(center)
                .auth(auth)
                .approval(approval)
                .build();
    }

    public static Teacher createTeacher(Long id, String name, Center center, Approval approval, Auth auth) {
        return Teacher.builder()
                .id(id)
                .name(name)
                .center(center)
                .approval(approval)
                .auth(auth)
                .build();
    }

    public static Theme englishAndCoding(){
        return Theme.builder()
                .english(true)
                .coding(true)
                .build();
    }

    public static Theme english(){
        return Theme.builder()
                .english(true)
                .build();
    }

    public static Theme coding(){
        return Theme.builder()
                .coding(true)
                .build();
    }

    public static Center createCenter(Long centerId, String name, Boolean signed, Boolean recruit, Theme theme) {
        Center center = Center.builder()
                .id(centerId)
                .name(name)
                .signed(signed)
                .recruit(recruit)
                .theme(theme)
                .build();
        return center;
    }

    public static Center createCenter(Long centerId, String name) {
        Center center = Center.builder()
                .id(centerId)
                .name(name)
                .area(new Area())
                .build();
        return center;
    }

    public static Center createCenter(String name) {
        Center center = Center.builder()
                .name(name)
                .build();
        return center;
    }

    public static Center createCenter(String name, Integer score, Double latitude, Double longitude) {
        Center center = Center.builder()
                .name(name)
                .score(score)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        return center;
    }

    public static Center createCenter(Long id, String name, Integer score, Double latitude, Double longitude) {
        Center center = Center.builder()
                .id(id)
                .name(name)
                .score(score)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        return center;
    }


    public static Center createKindergarten(String name, Integer score, Double latitude, Double longitude) {
        return Kindergarten.kBuilder()
                .name(name)
                .score(score)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

    public static Center createChildHouse(String name, Integer score, Double latitude, Double longitude) {
        return ChildHouse.cBuilder()
                .name(name)
                .score(score)
                .longitude(longitude)
                .latitude(latitude)
                .build();
    }

    public static Center createCenter(String name, Boolean signed, Boolean recruit, Theme theme) {
        Center center = Center.builder()
                .name(name)
                .signed(signed)
                .recruit(recruit)
                .theme(theme)
                .build();
        return center;
    }

    public static Center createCenter(String name, Area area, Theme theme, Integer maxAge, Integer minAge) {
        Center center = Center.builder()
                .name(name)
                .area(area)
                .minAge(minAge)
                .maxAge(maxAge)
                .theme(theme)
                .build();
        return center;
    }

    public static Review createReview(Long reviewId, Center center, Integer score){
        return Review.builder()
                .id(reviewId)
                .center(center)
                .score(score)
                .build();
    }

    public static Review createReview(Center center, Integer score){
        return Review.builder()
                .center(center)
                .score(score)
                .build();
    }

    public static Review createReview(Long id, Center center, Integer score, Parent parent, Teacher teacher, String content){
        return Review.builder()
                .id(id)
                .center(center)
                .score(score)
                .parent(parent)
                .teacher(teacher)
                .content(content)
                .build();
    }
    public static Review createReview(Center center, Integer score, Parent parent, Teacher teacher, String content){
        return Review.builder()
                .center(center)
                .score(score)
                .parent(parent)
                .teacher(teacher)
                .content(content)
                .build();
    }

    public static Parent createParent(){
        return Parent.builder()
                .build();
    }

    public static Prefer createPrefer(Long preferId, Parent parent, Center center){
        return Prefer.builder()
                .id(preferId)
                .parent(parent)
                .center(center)
                .build();
    }

    public static Prefer createPrefer(Parent parent, Center center){
        return Prefer.builder()
                .parent(parent)
                .center(center)
                .build();
    }

    public static Teacher createTeacher() {
        return Teacher.builder()
                .build();
    }

    public static Teacher createTeacher(Long id, Center center, Auth auth, Approval approval) {
        return Teacher.builder()
                .id(id)
                .center(center)
                .auth(auth)
                .approval(approval)
                .build();
    }

    public static Teacher createTeacher(Long id) {
        return Teacher.builder()
                .id(id)
                .build();
    }

    public static String createJwtToken(User user){
        return JWT.create()
                .withSubject("JWT")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 60 * 3))) // JWT 만료시간 밀리세컨단위
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512("secretKey"));
    }

    public static Presentation createValidPresentation(Center center) {
        return Presentation.builder()
                .center(center)
                .content("test 설명회 입니다")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .build();
    }

    public static Presentation createValidPresentation() {
        return Presentation.builder()
                .id(1L)
                .content("test 설명회 입니다")
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(2))
                .build();
    }

    public static Presentation createValidPresentation(Center center, long minusDaysForStartDay, long plusDaysForEndDay) {
        return Presentation.builder()
                .center(center)
                .content("test 설명회 입니다")
                .startDate(LocalDate.now().minusDays(minusDaysForStartDay))
                .endDate(LocalDate.now().plusDays(plusDaysForEndDay))
                .build();
    }

    public static Presentation createInvalidPresentation(Center center) {
        return Presentation.builder()
                .center(center)
                .content("test 설명회 입니다")
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now().minusDays(1))
                .build();
    }

    public static Presentation createInvalidPresentation(Center center, long minusDaysForStartDay, long minusDaysForEndDay) {
        return Presentation.builder()
                .center(center)
                .content("test 설명회 입니다")
                .startDate(LocalDate.now().minusDays(minusDaysForStartDay))
                .endDate(LocalDate.now().minusDays(minusDaysForEndDay))
                .build();
    }

    public static PtDate createCanRegisterPtDate(Presentation presentation) {
        return PtDate.builder()
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation)
                .build();
    }

    public static PtDate createCanRegisterPtDate(Long id ,Presentation presentation) {
        return PtDate.builder()
                .id(id)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(1)
                .waitingCnt(0)
                .presentation(presentation)
                .build();
    }

    public static PtDate createNoParticipantsPtDate(Long id ,Presentation presentation) {
        return PtDate.builder()
                .id(id)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(0)
                .waitingCnt(0)
                .presentation(presentation)
                .build();
    }



    public static PtDate createPtDate(Long id){
        return PtDate.builder()
                .id(id)
                .build();
    }

    public static PtDate createCanNotRegisterPtDate(Presentation presentation) {
        return PtDate.builder()
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(3)
                .waitingCnt(1)
                .presentation(presentation)
                .build();
    }

    public static PtDate createCanNotRegisterPtDate(Long id, Presentation presentation) {
        return PtDate.builder()
                .id(id)
                .date(LocalDate.now())
                .time("오후 9시")
                .ablePersonNum(3)
                .participantCnt(3)
                .waitingCnt(1)
                .presentation(presentation)
                .build();
    }


    public static Participation createJoinParticipation(PtDate ptDate, Parent parent) {
        return Participation.builder()
                .ptDate(ptDate)
                .parent(parent)
                .status(Status.JOINED)
                .build();
    }

    public static Participation createCancelParticipation(PtDate ptDate, Parent parent) {
        return Participation.builder()
                .ptDate(ptDate)
                .parent(parent)
                .status(Status.CANCELED)
                .build();
    }

    public static Comment createComment(Long id, Boolean anonymous, String content, Post post, User user) {
        return Comment.builder()
                .id(id)
                .anonymous(anonymous)
                .content(content)
                .post(post)
                .user(user)
                .parentComment(null)
                .subComments(new ArrayList<>())
                .commentHearts(new ArrayList<>())
                .heartCnt(0)
                .anonymousOrder(1)
                .build();
    }

    public static Comment createComment(Boolean anonymous, String content, Post post, User user) {
        return Comment.builder()
                .anonymous(anonymous)
                .content(content)
                .post(post)
                .user(user)
                .build();
    }

    public static CommentHeart createCommentHeart(Long id, User user, Comment comment) {
        return CommentHeart.builder()
                .id(id)
                .user(user)
                .comment(comment)
                .build();
    }
    public static CommentHeart createCommentHeart(User user, Comment comment) {
        return CommentHeart.builder()
                .user(user)
                .comment(comment)
                .build();
    }

    public static Waiting createWaiting(PtDate ptDate, Parent parent, Integer waitingOrder) {
        return Waiting.builder()
                .ptDate(ptDate)
                .parent(parent)
                .waitingOrder(waitingOrder)
                .build();
    }

    public static Waiting createWaiting(Long id, PtDate ptDate, Parent parent, Integer waitingOrder) {
        return Waiting.builder()
                .id(id)
                .ptDate(ptDate)
                .parent(parent)
                .waitingOrder(waitingOrder)
                .build();
    }

    public static AuthNumber createAuthNumber(AuthKind authKind) {
        AuthNumber build = AuthNumber.builder()
                .id(-1L)
                .phoneNum("phoneNum")
                .authNum("1234")
                .authKind(authKind)
                .build();
        build.setCreatedDateForTest(LocalDateTime.now());
        return build;
    }

    public static ReviewHeart createReviewHeart(Long id, Review review, User user) {
        return ReviewHeart.builder()
                .id(id)
                .review(review)
                .user(user)
                .build();
    }

    public static Theme createTheme() {
        return Theme.builder()
                .animal(true)
                .art(true)
                .build();
    }

    public static Waiting createWaiting(Long id) {
        return Waiting.builder()
                .id(id)
                .build();
    }
    public static Scrap createScrap(Long id, User user, String name) {
        return Scrap.builder()
                .id(id)
                .user(user)
                .name(name)
                .isDefault(false)
                .build();
    }

    public static Scrap createDefaultScrap(Long id, User user, String name) {
        return Scrap.builder()
                .id(id)
                .user(user)
                .name(name)
                .isDefault(true)
                .build();
    }

    public static ScrapPost createScrapPost(Long id, Post post, Scrap scrap) {
        return ScrapPost.builder()
                .id(id)
                .post(post)
                .scrap(scrap)
                .build();
    }

    public static Board createBoard(Long id, String name, Center center, Boolean isDefault) {
        return Board.builder()
                .id(id)
                .name(name)
                .center(center)
                .isDefault(isDefault)
                .build();
    }

    public static Child createChild(Long id, String name, Parent parent, Center center, Approval approval) {
        return Child.builder()
                .id(id)
                .name(name)
                .parent(parent)
                .center(center)
                .approval(approval)
                .build();
    }
    public static Child createChild(String name, Parent parent, Center center, Approval approval) {
        return Child.builder()
                .name(name)
                .parent(parent)
                .center(center)
                .approval(approval)
                .build();
    }

    public static Bookmark createBookmark(Long id, Board board, User user) {
        return Bookmark.builder()
                .id(id)
                .board(board)
                .user(user)
                .build();
    }

    public static Center createCenter(String name, boolean signed, Area area) {
        return Center.builder()
                .name(name)
                .signed(signed)
                .area(area)
                .build();
    }

    public static Center createKindergarten(String name, boolean signed, Area area) {
        return Kindergarten.kBuilder()
                .name(name)
                .signed(signed)
                .area(area)
                .build();
    }

    public static Teacher createTeacher(String name, Center center, Approval approval) {
        return Teacher.builder()
                .name(name)
                .center(center)
                .approval(approval)
                .build();
    }

    public static Report createReport(Long id, Long targetId){
        return Report.builder()
                .id(id)
                .targetId(targetId)
                .build();
    }

    public static Report createReport(Long targetId, User targetUser){
        return Report.builder()
                .targetId(targetId)
                .targetUser(targetUser)
                .build();
    }

    public static ReportDetailPost createReportDetailPost(Long id, User user, Post post) {
        return ReportDetailPost.builder()
                .id(id)
                .user(user)
                .post(post)
                .build();
    }

    public static ReportDetailPost createReportDetailPost(Report report, User user, Post post, String dtype) {
        return ReportDetailPost.builder()
                .report(report)
                .user(user)
                .post(post)
                .dtype(dtype)
                .build();
    }

    public static ReportDetailComment createReportDetailComment(Report report, User user, Comment comment, String dtype) {
        return ReportDetailComment.builder()
                .report(report)
                .user(user)
                .comment(comment)
                .dtype(dtype)
                .build();
    }

    public static ReportDetailComment createReportDetailComment(Long id, User user, Comment comment) {
        return ReportDetailComment.builder()
                .id(id)
                .user(user)
                .comment(comment)
                .build();
    }
}
