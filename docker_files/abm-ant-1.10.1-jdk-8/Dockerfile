FROM sgrio/java-oracle:jdk_8_unlimited

ENV DEBIAN_FRONTEND noninteractive

ENV ANT_VERSION 1.10.1

ENV ANT_HOME /opt/apache-ant
ENV PATH ${ANT_HOME}/bin:$PATH

RUN curl --silent --location --retry 3 \
		http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz \
		| tar xz -C /tmp && \
	mv /tmp/apache-ant-${ANT_VERSION} /opt/apache-ant-${ANT_VERSION} && \
	ln -s /opt/apache-ant-${ANT_VERSION} ${ANT_HOME} && \
	rm -rf /tmp/* \
        && echo "Adding ant user and group" \
	&& groupadd --system --gid 1000 ant \
	&& useradd --system --gid ant --uid 1000 --shell /bin/bash --create-home ant \
	&& mkdir /home/ant/.ant \
	&& chown --recursive ant:ant /home/ant \
	\
	&& echo "Symlinking root ant cache to ant ant cache" \
	&& ln -s /home/ant/.ant /root/.ant

USER ant
VOLUME "/home/ant/.ant"

WORKDIR /home/ant
