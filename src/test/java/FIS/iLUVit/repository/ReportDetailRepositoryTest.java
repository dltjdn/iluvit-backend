package FIS.iLUVit.repository;

import FIS.iLUVit.Creator;
import FIS.iLUVit.config.argumentResolver.ForDB;
import FIS.iLUVit.domain.iluvit.Comment;
import FIS.iLUVit.domain.iluvit.Post;
import FIS.iLUVit.domain.iluvit.Teacher;
import FIS.iLUVit.domain.iluvit.enumtype.Approval;
import FIS.iLUVit.domain.iluvit.enumtype.ReportType;
import FIS.iLUVit.domain.iluvit.reports.Report;
import FIS.iLUVit.domain.iluvit.reports.ReportDetail;
import FIS.iLUVit.repository.iluvit.ReportDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(ForDB.class))
class ReportDetailRepositoryTest {

    @Autowired
    private ReportDetailRepository reportDetailRepository;
    @Autowired
    private EntityManager em;

    Teacher targetUser, user;
    Post post1;
    Comment comment1;
    Report reportPost, reportComment;
    ReportDetail reportDetailPost, reportDetailComment;

    @BeforeEach
    public void init(){
        targetUser = Creator.createTeacher("targetUser", null, Approval.ACCEPT);
        user = Creator.createTeacher("user", null, Approval.ACCEPT);

        post1 = Creator.createPost("title", "content", false,null, targetUser);
        comment1 = Creator.createComment(true, "content", post1, targetUser);

        reportPost = Creator.createReport(post1.getId(), targetUser);
        reportComment = Creator.createReport(comment1.getId(), targetUser);

        reportDetailPost = Creator.createReportDetailPost( reportPost, user, post1, ReportType.POST.toString());
        reportDetailComment = Creator.createReportDetailComment( reportComment, user, comment1, ReportType.COMMENT.toString());

        em.persist(targetUser);
        em.persist(user);
        em.persist(post1);
        em.persist(comment1);
        em.persist(reportPost);
        em.persist(reportComment);
        em.persist(reportDetailPost);
        em.persist(reportDetailComment);
        em.flush();
        em.clear();
    }

    @Test
    public void 신고상세내역조회_유저아이디_포스트아이디(){
        //given

        //when
        ReportDetail reportDetail = reportDetailRepository.findByUserIdAndTargetPostId(user.getId(), post1.getId())
                .orElse(null);
        //then
        assertThat(reportDetail).isNotNull();
        assertThat(reportDetail.getId()).isEqualTo(reportDetailPost.getId());
        assertThat(reportDetail.getUser()).isEqualTo(user);
    }

    @Test
    public void 신고상세내역조회_유저아이디_댓글아이디(){
        //given

        //when
        ReportDetail reportDetail = reportDetailRepository.findByUserIdAndTargetCommentId(user.getId(), comment1.getId())
                .orElse(null);
        //then
        assertThat(reportDetail).isNotNull();
        assertThat(reportDetail.getId()).isEqualTo(reportDetailComment.getId());
        assertThat(reportDetail.getUser()).isEqualTo(reportDetailComment.getUser());
    }
}