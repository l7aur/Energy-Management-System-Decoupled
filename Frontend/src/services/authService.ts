import { AUTH_ENDPOINTS, DEVICE_ENDPOINTS, USER_ENDPOINTS } from '@/api/EndPoints'
import type Auth from '@/model/Auth'
import type { MicroUser } from '@/model/UserModel'
import type UserModel from '@/model/UserModel'
import axios, { HttpStatusCode } from 'axios'
import { jwtDecode } from 'jwt-decode'

export async function loginUsernamePassword(request: LoginRequest): Promise<Auth> {
  try {
    const response1 = await axios.post<LoginReply>(AUTH_ENDPOINTS.LOGIN, {
      username: request.username,
      password: request.password,
    })
    if (response1.status !== HttpStatusCode.Ok) throw Error('err')

    const probe = await axios.get<MicroUser>(`${USER_ENDPOINTS.FIND_ONE}/${request.username}`, {
      headers: {
        Authorization: `Bearer ${response1.data.jwtToken}`,
      },
    })

    if (probe.data.username !== request.username) {
      const response2 = await axios.post<UserModel>(
        USER_ENDPOINTS.ADD_ONE,
        {
          id: null,
          username: request.username,
        },
        {
          headers: {
            Authorization: `Bearer ${response1.data.jwtToken}`,
          },
        },
      )
      if (response2.status !== HttpStatusCode.Ok) throw Error('User microservice failed')
    }

    const decoded: { role: string } = jwtDecode(response1.data.jwtToken)
    return {
      username: response1.data.username,
      token: response1.data.jwtToken,
      role: decoded.role,
    }
  } catch (error: unknown) {
    console.error('Login failed: ', error)
    return { username: '', token: '', role: '' }
  }
}

export async function registerUsernamePassword(request: RegisterRequest): Promise<string> {
  try {
    const response1 = await axios.post<RegisterReply>(AUTH_ENDPOINTS.REGISTER, {
      id: null,
      username: request.username,
      password: request.password,
      role: request.role,
    })
    if (response1.status !== HttpStatusCode.Created) throw new Error('call1')
    const response2 = await axios.post<MicroUser>(
      USER_ENDPOINTS.ADD_ONE,
      { id: null, username: request.username },
      {
        headers: {
          Authorization: `Bearer ${response1.data.token}`,
        },
      },
    )
    if (response2.status !== HttpStatusCode.Created) throw new Error('call2')
    const response3 = await axios.post<MicroUser>(
      DEVICE_ENDPOINTS.ADD_ONE_USER,
      {
        id: null,
        username: request.username,
      },
      {
        headers: {
          Authorization: `Bearer ${response1.data.token}`,
        },
      },
    )
    if (response3.status !== HttpStatusCode.Created) throw new Error('call3')

    return response1.data.errorMessage
  } catch (error: unknown) {
    console.error('Registration failed: ', error)
    return 'Error'
  }
}

interface LoginRequest {
  username: string
  password: string
}

interface LoginReply {
  username: string
  jwtToken: string
}

interface RegisterRequest {
  username: string
  password: string
  role: string
}

interface RegisterReply {
  errorMessage: string
  token: string
}
