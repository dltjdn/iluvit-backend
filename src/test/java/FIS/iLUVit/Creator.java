package FIS.iLUVit;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;
import FIS.iLUVit.domain.enumtype.AuthKind;

import java.time.LocalDateTime;

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


}
