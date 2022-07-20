package FIS.iLUVit;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.AuthKind;
import FIS.iLUVit.domain.enumtype.Status;
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
                .hasProfileImg(false)
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
                .hasProfileImg(false)
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
                .build();
    }

    public static Post createPost(String title, String content, Boolean anonymous, Integer commentCnt, Integer heartCnt, Integer imgCnt, Integer videoCnt, Board board, User user) {
        return new Post(title, content, anonymous, commentCnt, heartCnt, imgCnt, videoCnt, board, user);
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
                .sign(Algorithm.HMAC512("symmetricKey"));
    }

    public static Presentation createValidPresentation(Center center) {
        return Presentation.builder()
                .center(center)
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
}
