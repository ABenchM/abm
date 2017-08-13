package de.fraunhofer.abm.docker.Hermes;

import java.util.concurrent.ExecutorService;

import de.fraunhofer.abm.builder.docker.base.AbstractDockerStep;
import de.fraunhofer.abm.domain.RepositoryDTO;

public class ExtractResults extends AbstractDockerStep<Void> {

	public ExtractResults(RepositoryDTO repo, ExecutorService executor) {
		super(repo, executor);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Void execute() {
		// TODO Auto-generated method stub
		return null;
	}

}
