package com.gamerstore.user_service.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.stripe.model.Customer;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.gamerstore.user_service.dto.UsuarioRequest;
import com.gamerstore.user_service.dto.UsuarioResponse;
import com.gamerstore.user_service.entity.Usuario;
import com.gamerstore.user_service.mapper.UsuarioMapper;
import com.gamerstore.user_service.repository.UsuarioRepository;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    private BCryptPasswordEncoder passwordEncoder;

    private MockedStatic<Customer> customerMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();

        // 🔥 MOCK GLOBAL DO STRIPE
        customerMock = mockStatic(Customer.class);
    }

    @AfterEach
    void tearDown() {
        customerMock.close();
    }

    // ===============================
    // ✅ CADASTRAR USUÁRIO
    // ===============================
    @Test
    void cadastrarUsuario_sucesso() {

        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Joaozinho");
        request.setEmail("joaozinho@gmail.com");
        request.setSenha("123456");

        Usuario usuarioEntity = new Usuario();
        usuarioEntity.setNome(request.getNome());
        usuarioEntity.setEmail(request.getEmail());
        usuarioEntity.setSenha(request.getSenha());

        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioMapper.toEntity(request)).thenReturn(usuarioEntity);

        // ⚠️ NÃO mocka save nem response porque não vai chegar lá

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.cadastrarUsuario(request);
        });

        assertTrue(exception.getMessage().contains("Erro ao criar usuário na Stripe"));
    }

    @Test
    void cadastrarUsuario_emailJaCadastrado_lancaException() {
        UsuarioRequest request = new UsuarioRequest();
        request.setEmail("teste@gmail.com");

        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cadastrarUsuario(request));
    }

    // ===============================
    // 🔐 LOGIN
    // ===============================
    @Test
    void logar_sucesso() {

        String senha = "123456";

        Usuario usuario = new Usuario();
        usuario.setEmail("teste@gmail.com");
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setNome("Vanderson");

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.toResponse(usuario))
                .thenReturn(new UsuarioResponse());

        UsuarioResponse response = usuarioService.logar(usuario.getEmail(), senha);

        assertNotNull(response);
    }

    @Test
    void logar_usuarioNaoEncontrado_lancaException() {

        when(usuarioRepository.findByEmail("naoexiste@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.logar("naoexiste@gmail.com", "123"));
    }

    @Test
    void logar_senhaInvalida_lancaException() {

        Usuario usuario = new Usuario();
        usuario.setEmail("teste@gmail.com");
        usuario.setSenha(passwordEncoder.encode("123456"));

        when(usuarioRepository.findByEmail(usuario.getEmail()))
                .thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.logar(usuario.getEmail(), "errada"));
    }

    // ===============================
    // 🔄 ATUALIZAR
    // ===============================
    @Test
    void atualizarUsuario_comNovaSenha_sucesso() {

        Long id = 1L;

        UsuarioRequest request = new UsuarioRequest();
        request.setNome("NovoNome");
        request.setSenha("novaSenha");

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("AntigoNome");
        usuario.setSenha(passwordEncoder.encode("123456"));

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        when(usuarioMapper.toResponse(usuario))
                .thenReturn(new UsuarioResponse());

        UsuarioResponse response = usuarioService.atualizarUsuario(id, request);

        assertNotNull(response);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);

        verify(usuarioRepository).save(captor.capture());

        Usuario atualizado = captor.getValue();

        assertTrue(passwordEncoder.matches("novaSenha", atualizado.getSenha()));
    }

    @Test
    void atualizarUsuario_semNovaSenha_sucesso() {

        Long id = 1L;

        UsuarioRequest request = new UsuarioRequest();
        request.setNome("NovoNome");

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("AntigoNome");
        usuario.setSenha(passwordEncoder.encode("123456"));

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenReturn(usuario);

        when(usuarioMapper.toResponse(usuario))
                .thenReturn(new UsuarioResponse());

        UsuarioResponse response = usuarioService.atualizarUsuario(id, request);

        assertNotNull(response);

        assertTrue(passwordEncoder.matches("123456", usuario.getSenha()));
    }

    @Test
    void atualizarUsuario_usuarioNaoEncontrado_lancaException() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.atualizarUsuario(1L, new UsuarioRequest()));
    }

    // ===============================
    // 🗑 DELETE
    // ===============================
    @Test
    void deletarUsuario_sucesso() {

        Long id = 1L;

        when(usuarioRepository.existsById(id)).thenReturn(true);

        usuarioService.deletarUsuario(id);

        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    void deletarUsuario_usuarioNaoEncontrado_lancaException() {

        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.deletarUsuario(1L));
    }
}