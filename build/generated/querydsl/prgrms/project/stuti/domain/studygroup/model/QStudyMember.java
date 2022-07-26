package prgrms.project.stuti.domain.studygroup.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudyMember is a Querydsl query type for StudyMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyMember extends EntityPathBase<StudyMember> {

    private static final long serialVersionUID = -2026191621L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStudyMember studyMember = new QStudyMember("studyMember");

    public final prgrms.project.stuti.global.base.QBaseEntity _super = new prgrms.project.stuti.global.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final prgrms.project.stuti.domain.member.model.QMember member;

    public final QStudyGroup studyGroup;

    public final EnumPath<StudyMemberRole> studyMemberRole = createEnum("studyMemberRole", StudyMemberRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QStudyMember(String variable) {
        this(StudyMember.class, forVariable(variable), INITS);
    }

    public QStudyMember(Path<? extends StudyMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStudyMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStudyMember(PathMetadata metadata, PathInits inits) {
        this(StudyMember.class, metadata, inits);
    }

    public QStudyMember(Class<? extends StudyMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new prgrms.project.stuti.domain.member.model.QMember(forProperty("member")) : null;
        this.studyGroup = inits.isInitialized("studyGroup") ? new QStudyGroup(forProperty("studyGroup")) : null;
    }

}

