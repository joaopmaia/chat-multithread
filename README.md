ESTE CHAT FOI FEITO COM INTUITO DE SE ESTUDAR
O CHAT NÃO ESTÁ BEM IMPLEMENTADO E NÃO DEVE SER USADO COMO EXEMPLO, É APENAS UMA ATIVIDADE DE ESTUDO

Implementação Java de um servidor de bate-papo multithread com quadro branco compartilhado

Descrição do arquivo:

- ChatMessage.java: especifica o formato das mensagens de chat trocadas entre o cliente <-> servidor
- ChatServer.java: Implementa servidor de bate-papo multi-threaded. A comunicação entre vários clientes acontece através do servidor de chat
- Client.java: Implementa o cliente e a GUI com botões de conexão / desconexão, Lista com clientes ativos, Área de Texto, Área de Mensagem 


Uso:
- Compile o código: javac * .java
- Inicie o Chat Server a partir da linha de comando: java ChatServer
- Inicie um cliente a partir da linha de comando em uma máquina (terminal) diferente: Java Client
- Digite o nome de usuário no campo de texto do canto superior esquerdo da janela de bate-papo do cliente e pressione o botão conectar (VERIFICAR IMAGEM 1)
- Inicie o 2º / 3º ou mais Clientes, usando as duas etapas acima.
- Troque mensagens digitando no campo de texto inferior e pressionando o botão Enter do teclado (VERIFICAR IMAGEM 2).


Screnshots: 
- Na IMAGEM1 pode-se ver, na imagem, onde deve ser escrito o nome do usuário
- Na IMAGEM2 pode-se ver, na imagem, onde deve ser escrito as mensagens do usuário
- Na IMAGEM3 pode-se ver, na imagem, um exemplo do chat funcionando no terminal onde executou-se o server
- Na IMAGEM3 pode-se ver, ainda, as diferentes threads dos Clients e suas mensagens
