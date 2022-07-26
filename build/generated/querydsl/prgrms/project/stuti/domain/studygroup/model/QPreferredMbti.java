package prgrms.project.stuti.domain.studygroup.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPreferredMbti is a Querydsl query type for PreferredMbti
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPreferredMbti extends EntityPathBase<PreferredMbti> {

    private static final long serialVersionUID = 1934028835L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPreferredMbti preferredMbti = new QPreferredMbti("preferredMbti");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<prgrms.project.stuti.domain.member.model.Mbti> mbti = createEnum("mbti", prgrms.project.stuti.domain.member.model.Mbti.class);

    public final QStudyGroup studyGroup;

    public QPreferredMbti(String variable) {
        this(PreferredMbti.class, forVariable(variable), INITS);
    }

    public QPreferredMbti(Path<? extends PreferredMbti> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPreferredMbti(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPreferredMbti(PathMetadata metadata, PathInits inits) {
        this(PreferredMbti.class, metadata, inits);
    }

    public QPreferredMbti(Class<? extends PreferredMbti> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.studyGroup = inits.isInitialized("studyGroup") ? new QStudyGroup(forProperty("studyGroup")) : null;
    }

}

