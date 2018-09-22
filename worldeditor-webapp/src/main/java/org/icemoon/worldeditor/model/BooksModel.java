package org.icemoon.worldeditor.model;

import org.apache.wicket.model.IModel;
import org.icemoon.eartheternal.common.Book;
import org.icemoon.eartheternal.common.Books;
import org.icemoon.eartheternal.common.IDatabase;
import org.icemoon.worldeditor.AbstractPage;

@SuppressWarnings("serial")
public final class BooksModel extends AbstractDatabaseModel<Books, Book, Long, String, IDatabase> {
	public BooksModel(AbstractPage page) {
		super(page);
	}

	public BooksModel(IModel<? extends IDatabase> database) {
		super(database);
	}

	public Books getObject() {
		return database.getObject().getBooks();
	}
}