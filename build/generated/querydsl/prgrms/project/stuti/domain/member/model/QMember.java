package prgrms.project.stuti.domain.member.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -634676194L;

    public static final QMember member = new QMember("member1");

    public final prgrms.project.stuti.global.base.QBaseTimeEntity _super = new prgrms.project.stuti.global.base.QBaseTimeEntity(this);

    public final StringPath blogUrl = createString("blogUrl");

    public final EnumPath<Career> career = createEnum("career", Career.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final EnumPath<Field> field = createEnum("field", Field.class);

    public final StringPath githubUrl = createString("githubUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<MemberRole> memberRole = createEnum("memberRole", MemberRole.class);

    public final StringPath nickName = createString("nickName");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

