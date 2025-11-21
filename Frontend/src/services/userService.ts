import { AUTH_ENDPOINTS, DEVICE_ENDPOINTS, USER_ENDPOINTS } from '@/api/EndPoints'
import type { MicroAuth, MicroUser } from '@/model/UserModel'
import type UserModel from '@/model/UserModel'
import { useAuthStore } from '@/stores/auth'
import axios, { HttpStatusCode } from 'axios'

export async function findAll(): Promise<UserModel[]> {
  const auth = useAuthStore()
  try {
    const response1 = await axios.get<MicroUser[]>(USER_ENDPOINTS.FIND_ALL, {
      headers: {
        Authorization: `Bearer ${auth.token}`,
      },
    })
    if (response1.status !== HttpStatusCode.Ok) throw new Error('call 1')
    const response2 = await axios.get<MicroAuth[]>(AUTH_ENDPOINTS.FIND_ALL, {
      headers: {
        Authorization: `Bearer ${auth.token}`,
      },
    })
    if (response2.status !== HttpStatusCode.Ok) throw new Error('call 2')

    const combined: UserModel[] = response1.data.map((u) => {
      const a = response2.data.find((auth) => auth.username === u.username)
      return { u, a: a ?? { id: 0, username: '', password: '', role: '' } }
    })

    return combined
  } catch (error: unknown) {
    console.error('Find all users failed: ', error)
    return []
  }
}

export async function add(user: UserModel): Promise<UserModel> {
  const auth = useAuthStore()
  try {
    const response1 = await axios.post<MicroUser>(
      USER_ENDPOINTS.ADD_ONE,
      {
        id: null,
        username: user.u.username,
      },
      {
        headers: {
          Authorization: `Bearer ${auth.token}`,
        },
      },
    )
    if (response1.status !== HttpStatusCode.Created) throw new Error('call1')
    const response2 = await axios.post<MicroAuth>(
      AUTH_ENDPOINTS.REGISTER,
      {
        id: null,
        username: user.a.username,
        password: user.a.password,
        role: user.a.role,
      },
      {
        headers: {
          Authorization: `Bearer ${auth.token}`,
        },
      },
    )
    if (response2.status !== HttpStatusCode.Created) throw new Error('call2')
    const response3 = await axios.post<MicroUser>(
      DEVICE_ENDPOINTS.ADD_ONE_USER,
      {
        id: null,
        username: user.a.username,
      },
      {
        headers: {
          Authorization: `Bearer ${auth.token}`,
        },
      },
    )
    if (response3.status !== HttpStatusCode.Created) throw new Error('call3')

    return {
      u: { id: response1.data.id, username: response1.data.username },
      a: {
        id: response2.data.id,
        username: response2.data.username,
        password: response2.data.password,
        role: response2.data.role,
      },
    }
  } catch (error: unknown) {
    console.error('Adding new user failed: ', error)
    return { u: { id: 0, username: '' }, a: { id: 0, username: '', password: '', role: '' } }
  }
}

export async function update(user: UserModel): Promise<UserModel> {
  const auth = useAuthStore()
  try {
    const response1 = await axios.put<MicroUser>(
      USER_ENDPOINTS.ADD_ONE,
      {
        id: user.u.id,
        username: user.u.username,
      },
      {
        headers: {
          Authorization: `Bearer ${auth.token}`,
        },
      },
    )
    if (response1.status !== HttpStatusCode.Ok) throw new Error('call1')
    const response2 = await axios.put<MicroAuth>(
      AUTH_ENDPOINTS.UPDATE,
      {
        id: user.a.id,
        username: user.a.username,
        password: user.a.password,
        role: user.a.role,
      },
      {
        headers: {
          Authorization: `Bearer ${auth.token}`,
        },
      },
    )
    if (response2.status !== HttpStatusCode.Ok) throw new Error('call2')
    return {
      u: { id: response1.data.id, username: response1.data.username },
      a: {
        id: response2.data.id,
        username: response2.data.username,
        password: response2.data.password,
        role: response2.data.role,
      },
    }
  } catch (error: unknown) {
    console.error('Updating user failed: ', error)
    return { u: { id: 0, username: '' }, a: { id: 0, username: '', password: '', role: '' } }
  }
}

export async function remove(users: UserModel[]): Promise<boolean> {
  const auth = useAuthStore()
  try {
    const response1 = await axios.delete<number[]>(USER_ENDPOINTS.DELETE, {
      data: { ids: users.map((user) => user.u.id) },
      headers: {
        Authorization: `Bearer ${auth.token}`,
      },
    })
    if (response1.status !== HttpStatusCode.Ok) throw new Error('call 1')

    const response2 = await axios.delete<number[]>(AUTH_ENDPOINTS.DELETE, {
      data: { ids: users.map((user) => user.a.id) },
      headers: {
        Authorization: `Bearer ${auth.token}`,
      },
    })
    if (response2.status !== HttpStatusCode.Ok) throw new Error('call 2')

    const response3 = await axios.delete<string[]>(DEVICE_ENDPOINTS.DELETE_USER, {
      data: { usernames: users.map((user) => user.a.username) },
      headers: {
        Authorization: `Bearer ${auth.token}`,
      },
    })
    if (response3.status !== HttpStatusCode.Ok) throw new Error('call 3')

    return true
  } catch (error: unknown) {
    console.error('Deleting user failed:', error)
    return false
  }
}
