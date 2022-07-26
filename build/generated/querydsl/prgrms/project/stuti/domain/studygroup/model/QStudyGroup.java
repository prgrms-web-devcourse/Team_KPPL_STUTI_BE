package prgrms.project.stuti.domain.studygroup.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudyGroup is a Querydsl query type for StudyGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudyGroup extends EntityPathBase<StudyGroup> {

    private static final long serialVersionUID = -1455985666L;

    public static final QStudyGroup studyGroup = new QStudyGroup("studyGroup");

    public final prgrms.project.stuti.global.base.QBaseEntity _super = new prgrms.project.stuti.global.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final BooleanPath isOnline = createBoolean("isOnline");

    public final NumberPath<Integer> numberOfMembers = createNumber("numberOfMembers", Integer.class);

    public final NumberPath<Integer> numberOfRecruits = createNumber("numberOfRecruits", Integer.class);

    public final EnumPath<Region> region = createEnum("region", Region.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public final EnumPath<Topic> topic = createEnum("topic", Topic.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QStudyGroup(String variable) {
        super(StudyGroup.class, forVariable(variable));
    }

    public QStudyGroup(Path<? extends StudyGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudyGroup(PathMetadata metadata) {
        super(StudyGroup.class, metadata);
    }

}

