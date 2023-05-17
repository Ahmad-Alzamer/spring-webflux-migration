/*
 * This file is generated by jOOQ.
 */
package victor.training.spring.flux.jooq;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import victor.training.spring.flux.jooq.tables.Comment;
import victor.training.spring.flux.jooq.tables.Post;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.comment</code>.
     */
    public final Comment COMMENT = Comment.COMMENT;

    /**
     * The table <code>public.post</code>.
     */
    public final Post POST = Post.POST;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        return Arrays.asList(
            Sequences.HIBERNATE_SEQUENCE
        );
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Comment.COMMENT,
            Post.POST
        );
    }
}