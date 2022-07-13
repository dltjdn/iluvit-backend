package FIS.iLUVit;

import FIS.iLUVit.domain.*;
import FIS.iLUVit.domain.embeddable.Area;
import FIS.iLUVit.domain.embeddable.BasicInfra;
import FIS.iLUVit.domain.embeddable.Theme;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Creator {

    public static User createUser(){
        return null;
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

    public static Kindergarten createKindergarten(Area area, String name, Theme theme, Integer minAge, Integer maxAge, String addInfo, String program, BasicInfra basicInfra){
        return Kindergarten.kBuilder()
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


}
