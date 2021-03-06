package de.fraunhofer.abm.zenodo;

import java.io.IOException;
import java.util.List;

import de.fraunhofer.abm.domain.CollectionDTO;
import de.fraunhofer.abm.domain.VersionDTO;

public interface ZenodoAPI {

	public boolean test();
	public Deposition getDeposition(Integer id);
	public List<Deposition> getDepositions();
	public Integer uploadCollectionToZenodo(VersionDTO version, CollectionDTO collection, String url) throws UnsupportedOperationException, IOException ;
	public Deposition updateDeposition(Deposition deposition);
	public void deleteDeposition(Integer id);
	public Deposition createDeposition(final Metadata m) throws UnsupportedOperationException, IOException ;
	public List<DepositionFile> getFiles(Integer depositionId);
	public DepositionFile uploadFile(String fileName, Integer depositionId) throws UnsupportedOperationException, IOException;
	public boolean discard(Integer id);
	
}
