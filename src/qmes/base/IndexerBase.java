package qmes.base;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;

public class IndexerBase {

	FieldType contentType = null;
	FieldType infoType = null;
	
	protected FieldType getContentType() {
		if (contentType == null) {
			contentType = new FieldType();
			contentType.setStored(true);
			contentType.setTokenized(true);
			contentType.setIndexOptions(IndexOptions.DOCS);
			contentType.setStoreTermVectors(true);
			contentType.setStoreTermVectorOffsets(true);
			contentType.setStoreTermVectorPayloads(true);
			contentType.setStoreTermVectorPositions(true);
		}
		return contentType;
	}
	
	protected FieldType getInfoType() {
		if (infoType == null) {
			infoType = new FieldType();
			infoType.setStored(true);
			infoType.setTokenized(false);
			infoType.setIndexOptions(IndexOptions.NONE);
			infoType.setStoreTermVectors(false);
			infoType.setStoreTermVectorOffsets(false);
			infoType.setStoreTermVectorPayloads(false);
			infoType.setStoreTermVectorPositions(false);
		}
		return infoType;
	}
}
